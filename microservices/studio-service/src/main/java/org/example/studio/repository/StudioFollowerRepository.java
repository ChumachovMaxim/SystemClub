package org.example.studio.repository;

import org.example.studio.entity.StudioFollower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudioFollowerRepository extends JpaRepository<StudioFollower, Long> {
    boolean existsByStudioIdAndUserId(UUID studioId, UUID userId);
    void deleteByStudioIdAndUserId(UUID studioId, UUID userId);
}