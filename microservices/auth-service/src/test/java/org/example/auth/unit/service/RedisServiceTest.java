//package org.example.auth.unit.service;
//
//import org.example.auth.service.RedisService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class RedisServiceTest {
//
//    @Mock private StringRedisTemplate redisTemplate;
//    @Mock private ValueOperations<String, String> valueOps;
//
//    @InjectMocks private RedisService redisService;
//
//    private String capturedKey;
//
//    @BeforeEach
//    void setUp() {
//        when(redisTemplate.opsForValue()).thenReturn(valueOps);
//
//        // Установка TTL через рефлексию
//        try {
//            var field = RedisService.class.getDeclaredField("refreshTokenTtl");
//            field.setAccessible(true);
//            field.set(redisService, 60000L);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to set refreshTokenTtl", e);
//        }
//    }
//
//    @Test
//    void createAndValidateRefreshToken() {
//        UUID userId = UUID.randomUUID();
//        String fingerprint = "ip|ua";
//
//        // Убираем when() — set() возвращает void
//        doAnswer(invocation -> {
//            capturedKey = invocation.getArgument(0);
//            return null;
//        }).when(valueOps).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
//
//        String token = redisService.createRefreshToken(userId, fingerprint);
//        assertThat(token).isNotNull();
//        assertThat(capturedKey).isEqualTo("refresh:" + token);
//
//        when(valueOps.get("refresh:" + token)).thenReturn(userId + "|" + fingerprint);
//
//        String[] result = redisService.validateAndConsumeRefreshToken(token, fingerprint);
//        assertThat(result[0]).isEqualTo(userId.toString());
//        verify(redisTemplate).delete("refresh:" + token);
//    }
//
//    @Test
//    void validate_wrongFingerprint_returnsNull() {
//        String token = "token123";
//        when(valueOps.get("refresh:" + token)).thenReturn("user123|wrong-fp");
//
//        String[] result = redisService.validateAndConsumeRefreshToken(token, "correct-fp");
//        assertThat(result).isNull();
//        verify(redisTemplate).delete("refresh:" + token);
//    }
//}