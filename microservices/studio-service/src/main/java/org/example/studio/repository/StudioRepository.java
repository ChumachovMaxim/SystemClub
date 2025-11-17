package org.example.studio.repository;

import org.example.studio.entity.Studio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface StudioRepository extends JpaRepository<Studio, UUID> {

    @Modifying
    @Query("UPDATE Studio s SET s.followersCount = s.followersCount + 1 WHERE s.id = :studioId")
    void incrementFollowersCount(UUID studioId);

    @Modifying
    @Query("UPDATE Studio s SET s.followersCount = s.followersCount - 1 WHERE s.id = :studioId AND s.followersCount > 0")
    void decrementFollowersCount(UUID studioId);
}