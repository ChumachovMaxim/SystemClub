package org.example.studio.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "studios")
@Data
public class Studio {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "studio_admins", joinColumns = @JoinColumn(name = "studio_id"))
    @Column(name = "admin_id")
    private Set<UUID> adminIds = new HashSet<>();

    @Column(name = "followers_count", nullable = false)
    private long followersCount = 0L;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
}