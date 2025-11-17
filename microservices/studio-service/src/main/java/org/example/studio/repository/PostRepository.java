package org.example.studio.repository;

import org.example.studio.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByStudioIdOrderByTimestampDesc(UUID studioId);
}