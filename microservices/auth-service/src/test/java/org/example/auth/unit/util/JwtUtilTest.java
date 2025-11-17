//package org.example.auth.unit.util;
//
//import org.example.auth.entity.Role;
//import org.example.auth.entity.User;
//import org.example.auth.repository.UserRepository;
//import org.example.auth.util.JwtUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.lang.reflect.Field;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class JwtUtilTest {
//
//    @Mock
//    private UserRepository userRepository;
//    @InjectMocks
//    private JwtUtil jwtUtil;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        Field secretField = JwtUtil.class.getDeclaredField("secretKeyString");
//        secretField.setAccessible(true);
//        secretField.set(jwtUtil, "your_256_bit_secret_key_here_1234567890abcdef");
//
//        Field expirationField = JwtUtil.class.getDeclaredField("EXPIRATION_TIME");
//        expirationField.setAccessible(true);
//        expirationField.set(jwtUtil, 3600000L);
//
//        jwtUtil.init(); // Инициализация SECRET_KEY
//    }
//
//    @Test
//    void generateAndValidateToken() {
//        UUID userId = UUID.randomUUID();
//        User user = new User();
//        user.setRole(Role.USER);
//
//
//        String token = jwtUtil.generateToken(userId, "USER");
//        String extractedId = jwtUtil.getUserIdFromToken(token);
//        String role = jwtUtil.getRoleFromToken(token);
//
//        assertThat(UUID.fromString(extractedId)).isEqualTo(userId);
//        assertThat(role).isEqualTo("USER");
//        assertThat(jwtUtil.validateToken(token)).isTrue();
//    }
//}