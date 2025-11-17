package org.example.studio.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "studio_followers", uniqueConstraints = @UniqueConstraint(columnNames = {"studio_id", "user_id"}))
@Data
public class StudioFollower {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "studio_id", nullable = false)
    private UUID studioId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}