//package org.example.auth.unit.service;
//
//import org.example.auth.service.KafkaProducerService;
//import org.example.events.user.UserCreatedEvent;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class KafkaProducerServiceTest {
//
//    @Mock private KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
//    @InjectMocks private KafkaProducerService producerService;
//
//    @Test
//    void sendUserCreated_success() {
//        String userId = UUID.randomUUID().toString();
//        CompletableFuture<SendResult<String, UserCreatedEvent>> future =
//                CompletableFuture.completedFuture(mock(SendResult.class));
//
//        when(kafkaTemplate.send(eq("user.events"), eq(userId), any(UserCreatedEvent.class)))
//                .thenReturn(future);
//
//        producerService.sendUserCreated(userId, "testuser", "USER");
//
//        verify(kafkaTemplate).send(eq("user.events"), eq(userId), any(UserCreatedEvent.class));
//    }
//}