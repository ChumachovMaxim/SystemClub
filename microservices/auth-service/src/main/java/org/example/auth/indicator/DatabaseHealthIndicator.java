package org.example.auth.indicator;

import org.example.auth.repository.UserRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    private final UserRepository userRepository;

    public DatabaseHealthIndicator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Health health() {
        try {
            userRepository.count();
            return Health.up().withDetail("database", "OK").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("database", "Failed").build();
        }
    }
}