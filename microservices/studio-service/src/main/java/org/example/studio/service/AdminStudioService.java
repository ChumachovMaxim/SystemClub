package org.example.studio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studio.dto.PostCreateDto;
import org.example.studio.dto.PostResponseDto;
import org.example.studio.dto.StudioResponseDto;
import org.example.studio.dto.StudioUpdateDto;
import org.example.studio.entity.Post;
import org.example.studio.entity.Studio;
import org.example.studio.exception.AccessDeniedException;
import org.example.studio.exception.StudioNotFoundException;
import org.example.studio.repository.StudioRepository;
import org.example.studio.repository.StudioUserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStudioService {

    private final StudioRepository studioRepo;
    private final StudioUserRepository userRepo;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    @Transactional
    @CacheEvict(value = "studio", key = "#id")
    public StudioResponseDto update(UUID id, StudioUpdateDto dto, UUID userId) {
        Studio studio = studioRepo.findById(id)
                .orElseThrow(() -> new StudioNotFoundException(id));

        if (!studio.getAdminIds().contains(userId)) {
            throw new AccessDeniedException("Not admin");
        }

        studio.setName(dto.getName());
        studio.setDescription(dto.getDescription());

        studio = studioRepo.save(studio);
        meterRegistry.counter("studio.updated").increment();
        return toDto(studio);
    }

    @Transactional
    @CacheEvict(value = "studio_posts", key = "#studioId")
    public PostResponseDto addPost(UUID studioId, PostCreateDto dto, UUID userId) {
        Studio studio = studioRepo.findById(studioId)
                .orElseThrow(() -> new StudioNotFoundException(studioId));

        if (!studio.getAdminIds().contains(userId)) {
            throw new AccessDeniedException("Not admin");
        }

        Post post = new Post();
        post.setStudioId(studioId);
        post.setContent(dto.getContent());
        post.setAuthorId(userId);
        post.setTimestamp(Instant.now());
        // Обработка attachment...

        meterRegistry.counter("studio.post.added").increment();
        return toPostDto(post);
    }

    @Transactional
    public void addAdmin(UUID studioId, UUID adminId, UUID requesterId) {
        Studio studio = studioRepo.findById(studioId)
                .orElseThrow(() -> new StudioNotFoundException(studioId));

        if (!studio.getAdminIds().contains(requesterId)) {
            throw new AccessDeniedException("Not admin");
        }
        if (!userRepo.existsById(adminId)) {
            throw new IllegalArgumentException("User not found");
        }

        studio.getAdminIds().add(adminId);
        studioRepo.save(studio);
        meterRegistry.counter("studio.admin.added").increment();
    }

    private StudioResponseDto toDto(Studio s) {
        StudioResponseDto dto = new StudioResponseDto();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setOwnerId(s.getOwnerId());
        dto.setAvatarUrl(s.getAvatarUrl());
        dto.setFollowersCount(s.getFollowersCount());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }

    private PostResponseDto toPostDto(Post p) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(p.getId());
        dto.setStudioId(p.getStudioId());
        dto.setContent(p.getContent());
        dto.setAuthorId(p.getAuthorId());
        dto.setTimestamp(p.getTimestamp());
        dto.setAttachmentUrl(p.getAttachmentUrl());
        return dto;
    }
}