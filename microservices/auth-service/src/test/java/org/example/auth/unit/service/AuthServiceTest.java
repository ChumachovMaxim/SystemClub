//package org.example.auth.unit.service;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.example.auth.dto.LoginRequest;
//import org.example.auth.dto.RefreshRequest;
//import org.example.auth.dto.RefreshResponse;
//import org.example.auth.dto.UserDto;
//import org.example.auth.entity.Role;
//import org.example.auth.entity.User;
//import org.example.auth.repository.UserRepository;
//import org.example.auth.service.AuthService;
//import org.example.auth.service.KafkaProducerService;
//import org.example.auth.service.RedisService;
//import org.example.auth.util.JwtUtil;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock private UserRepository userRepository;
//    @Mock private JwtUtil jwtUtil;
//    @Mock private BCryptPasswordEncoder passwordEncoder;
//    @Mock private RedisService redisService;
//    @Mock private KafkaProducerService kafkaProducer;
//
//    @InjectMocks private AuthService authService;
//
//
//    @Test
//    void register_success() {
//        UserDto dto = new UserDto();
//        dto.setUsername("testuser");
//        dto.setEmail("test@example.com");
//        dto.setPassword("pass123");
//        dto.setFirstName("Test");
//        dto.setLastName("User");
//
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
//            User u = invocation.getArgument(0);
//            u.setUserId(UUID.randomUUID()); // ← Установить ID
//            return u;
//        });
//
//        String result = authService.register(dto);
//
//        assertThat(result).isEqualTo("User registered successfully");
//        verify(kafkaProducer).sendUserCreated(any(), eq("testuser"), eq("USER"));
//    }
//
//    @Test
//    void register_duplicateUsername_throws() {
//        UserDto dto = new UserDto();
//        dto.setUsername("exists");
//        dto.setEmail("test@example.com");
//
//        when(userRepository.findByUsername("exists")).thenReturn(Optional.of(new User()));
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
//                () -> authService.register(dto));
//        assertThat(ex.getMessage()).isEqualTo("Username or email already exists");
//    }
//
//    @Test
//    void login_success() {
//        LoginRequest req = new LoginRequest();
//        req.setUsername("user");
//        req.setPassword("pass");
//
//        User user = new User();
//        user.setUserId(UUID.randomUUID());
//        user.setPassword("hashed");
//        user.setRole(Role.USER);
//
//        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
//        when(redisService.createRefreshToken(any(), any())).thenReturn("refresh-token");
//        when(jwtUtil.generateToken(any(UUID.class), eq("USER"))).thenReturn("access-token");
//
//        var response = authService.login(req, mock(HttpServletRequest.class));
//
//        assertThat(response.getAccessToken()).isEqualTo("access-token");
//        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
//    }
//
//    @Test
//    void login_invalidPassword_throws() {
//        LoginRequest req = new LoginRequest();
//        req.setUsername("user");
//        req.setPassword("wrong");
//
//        User user = new User();
//        user.setPassword("hashed");
//
//        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
//                () -> authService.login(req, mock(HttpServletRequest.class)));
//        assertThat(ex.getMessage()).isEqualTo("Invalid username or password");
//    }
//
//    @Test
//    void refresh_success() {
//        UUID userId = UUID.randomUUID();
//        String fingerprint = "null|"; // ← Дублируем поведение getFingerprint()
//        String oldToken = "old123";
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.getRemoteAddr()).thenReturn(null);
//        when(request.getHeader("User-Agent")).thenReturn(null);
//
//        when(redisService.validateAndConsumeRefreshToken(oldToken, fingerprint))
//                .thenReturn(new String[]{userId.toString(), fingerprint});
//        when(redisService.createRefreshToken(eq(userId), eq(fingerprint))).thenReturn("new123");
//        when(jwtUtil.generateToken(eq(userId), any())).thenReturn("new-access");
//
//        User user = new User();
//        user.setRole(Role.USER);
//        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
//
//        RefreshRequest req = new RefreshRequest();
//        req.setRefreshToken(oldToken);
//
//        RefreshResponse resp = authService.refresh(req, request);
//
//        assertThat(resp.getNewAccessToken()).isEqualTo("new-access");
//        assertThat(resp.getNewRefreshToken()).isEqualTo("new123");
//    }
//}