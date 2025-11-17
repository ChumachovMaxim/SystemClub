package org.example.studio.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class PostResponseDto {
    private UUID id;
    private UUID studioId;
    private String content;
    private UUID authorId;
    private Instant timestamp;
    private String attachmentUrl;
}