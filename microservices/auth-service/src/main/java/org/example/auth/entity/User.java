package org.example.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === НЕИЗМЕНЯЕМЫЙ ИДЕНТИФИКАТОР ДЛЯ МИКРОСЕРВИСОВ ===
    @Column(nullable = false, unique = true, updatable = false)
    private UUID userId;  // ← ВНЕШНИЙ КЛЮЧ ВО ВСЕХ СЕРВИСАХ

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;  // Hash with BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // === Генерация UUID при создании ===
    @PrePersist
    private void generateUserId() {
        if (this.userId == null) {
            this.userId = UUID.randomUUID();
        }
    }
}