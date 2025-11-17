package org.example.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootstrapServers = "localhost:9092";
    private String schemaRegistryUrl = "http://localhost:8081";
    private Consumer consumer = new Consumer();

    @Data
    public static class Consumer {
        private String groupId = "notification-group";
    }
}