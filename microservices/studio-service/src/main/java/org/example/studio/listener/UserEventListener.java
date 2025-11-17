package org.example.studio.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.user.UserCreatedEvent;
import org.example.studio.entity.StudioUser;
import org.example.studio.repository.StudioUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final StudioUserRepository userRepository;

    @KafkaListener(
            topics = "user.events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(UserCreatedEvent event) {
        String userId = event.getUserId().toString();
        log.info("Получено событие UserCreated: userId={}", userId);

        StudioUser localUser = new StudioUser(userId);

        try {
            userRepository.save(localUser);
            log.info("Локальный пользователь {} успешно сохранён.", userId);
        } catch (DataIntegrityViolationException e) {
            log.info("Пользователь {} уже существует — дубликат события проигнорирован.", userId);
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении пользователя {}: {}", userId, e.getMessage(), e);
        }
    }
}