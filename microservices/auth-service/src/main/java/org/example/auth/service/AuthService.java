package org.example.auth.service;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.auth.audit.entity.AuditType;
import org.example.auth.audit.service.AuditService;
import org.example.auth.dto.*;
import org.example.auth.entity.Role;
import org.example.auth.entity.User;
import org.example.auth.repository.UserRepository;
import org.example.auth.util.JwtUtil;
import org.example.events.user.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final KafkaProducerService kafkaProducer;
    private final AuditService auditService;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private String getFingerprint(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        return ip + "|" + (ua != null ? ua : "");
    }

    public String register(UserDto userDto) {
        log.info("Register attempt: username={}, email={}", userDto.getUsername(), userDto.getEmail());
        if (userRepository.findByUsername(userDto.getUsername()).isPresent() ||
                userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Username or email already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        // ОТПРАВЛЯЕМ СОБЫТИЕ
        kafkaProducer.sendUserCreated(
                user.getUserId().toString(),
                user.getUsername(),
                user.getRole().name()
        );

        log.info("User registered: userId={}", user.getUserId());
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        String ip = request.getRemoteAddr();
        log.warn("Login attempt: username={}, ip={}", username, ip);

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            auditService.log(username, ip, AuditType.LOGIN_FAILURE);
            throw new IllegalArgumentException("Invalid username or password");
        }

        auditService.log(username, user.getUserId().toString(), ip, AuditType.LOGIN_SUCCESS);

        String fingerprint = getFingerprint(request);
        String refreshToken = redisService.createRefreshToken(user.getUserId(), fingerprint);
        String accessToken = jwtUtil.generateToken(user.getUserId(), user.getRole().name());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        MDC.put("userId", user.getUserId().toString());
        log.info("Login success: userId={}", user.getUserId());
        MDC.remove("userId");

        return response;
    }

    public RefreshResponse refresh(RefreshRequest refreshRequest, HttpServletRequest request) {
        String token = refreshRequest.getRefreshToken();
        String currentIp = request.getRemoteAddr();
        String currentFingerprint = getFingerprint(request);

        log.debug("Refresh token attempt: token={}, ip={}", token, currentIp);

        RedisService.RefreshTokenResult result =
                redisService.validateAndConsumeRefreshToken(token, currentFingerprint);

        switch (result.status()) {
            case NOT_FOUND -> {
                auditService.log("unknown", currentIp, AuditType.REFRESH_TOKEN_NOT_FOUND);
                log.warn("Refresh failed: token not found or expired, ip={}", currentIp);
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }
            case INVALID_FORMAT -> {
                auditService.log("unknown", currentIp, AuditType.REFRESH_INVALID_FORMAT);
                log.warn("Refresh failed: corrupted token data, ip={}", currentIp);
                throw new IllegalArgumentException("Corrupted refresh token");
            }
            case FINGERPRINT_MISMATCH -> {
                auditService.log("unknown", result.parts()[0], currentIp, AuditType.REFRESH_FINGERPRINT_MISMATCH);
                log.warn("Refresh failed: fingerprint mismatch, stored={}, current={}, userId={}, ip={}",
                        result.parts()[1], currentFingerprint, result.parts()[0], currentIp);
                throw new IllegalArgumentException("Session compromised: device or IP changed");
            }
            case VALID -> {
                String userId = result.parts()[0];
                UUID uuid = UUID.fromString(userId);

                String userRole = userRepository.findByUserId(uuid)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"))
                        .getRole().name();

                String newRefreshToken = redisService.createRefreshToken(uuid, currentFingerprint);
                String newAccessToken = jwtUtil.generateToken(uuid, userRole);

                MDC.put("userId", userId);
                log.info("Token refreshed successfully");
                MDC.remove("userId");

                RefreshResponse response = new RefreshResponse();
                response.setNewAccessToken(newAccessToken);
                response.setNewRefreshToken(newRefreshToken);
                return response;
            }
            default -> throw new IllegalStateException("Unknown refresh status");
        }
    }
}