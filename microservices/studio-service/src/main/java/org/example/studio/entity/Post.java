package org.example.studio.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "studio_id", nullable = false)
    private UUID studioId;

    @Column(nullable = false)
    private String content;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "attachment_url")
    private String attachmentUrl;
}