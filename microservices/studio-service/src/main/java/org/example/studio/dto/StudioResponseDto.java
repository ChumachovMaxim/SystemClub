package org.example.studio.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class StudioResponseDto {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
    private String avatarUrl;
    private long followersCount;
    private Instant createdAt;
}