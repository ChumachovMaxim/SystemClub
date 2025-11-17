package org.example.auth.indicator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaHealthIndicator implements HealthIndicator {
    private final KafkaTemplate<String, ?> kafkaTemplate;

    public KafkaHealthIndicator(KafkaTemplate<String, ?> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Health health() {
        try {
            kafkaTemplate.execute(producer -> null);
            return Health.up().withDetail("kafka", "OK").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("kafka", "Failed").build();
        }
    }
}