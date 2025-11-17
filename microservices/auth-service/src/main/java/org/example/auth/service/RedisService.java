package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    @Value("${redis.refresh-token-ttl}")
    private long refreshTokenTtl;

    @PostConstruct
    public void init() {
        if (refreshTokenTtl <= 0) {
            throw new IllegalArgumentException("Refresh token TTL must be positive");
        }
    }

    public String createRefreshToken(UUID userId, String fingerprint) {
        String refreshToken = UUID.randomUUID().toString();
        String key = "refresh:" + refreshToken;
        String value = userId + "|" + fingerprint;
        stringRedisTemplate.opsForValue().set(key, value, refreshTokenTtl, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    public enum RefreshTokenStatus {
        VALID,
        NOT_FOUND,
        INVALID_FORMAT,
        FINGERPRINT_MISMATCH
    }

    public record RefreshTokenResult(String[] parts, RefreshTokenStatus status) {}

    public RefreshTokenResult validateAndConsumeRefreshToken(String refreshToken, String expectedFingerprint) {
        String key = "refresh:" + refreshToken;
        String stored = stringRedisTemplate.opsForValue().get(key);

        if (stored == null) {
            return new RefreshTokenResult(null, RefreshTokenStatus.NOT_FOUND);
        }

        String[] parts = stored.split("\\|", 2);
        if (parts.length != 2) {
            stringRedisTemplate.delete(key);
            return new RefreshTokenResult(null, RefreshTokenStatus.INVALID_FORMAT);
        }

        if (!parts[1].equals(expectedFingerprint)) {
            stringRedisTemplate.delete(key);
            return new RefreshTokenResult(null, RefreshTokenStatus.FINGERPRINT_MISMATCH);
        }

        stringRedisTemplate.delete(key);
        return new RefreshTokenResult(parts, RefreshTokenStatus.VALID);
    }


    public void cacheObject(String key, Object value, long ttl, TimeUnit unit) {
        objectRedisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    public <T> T getObject(String key, Class<T> type) {
        Object obj = objectRedisTemplate.opsForValue().get(key);
        return obj != null ? type.cast(obj) : null;
    }
}