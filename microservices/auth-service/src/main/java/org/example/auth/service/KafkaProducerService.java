package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.user.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "user.events";

    public void sendUserCreated(String userId, String username, String role) {
        UserCreatedEvent event = UserCreatedEvent.newBuilder()
                .setUserId(userId)
                .setUsername(username)
                .setRole(role)
                .setCreatedAt(System.currentTimeMillis())
                .build();

        CompletableFuture<SendResult<String, UserCreatedEvent>> future =
                kafkaTemplate.send(TOPIC, userId, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event sent: userId={}", userId);
            } else {
                log.error("Failed to send event: userId={}, error={}", userId, ex.getMessage(), ex);
            }
        });
    }

}