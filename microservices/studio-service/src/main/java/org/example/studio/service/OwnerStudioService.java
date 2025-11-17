package org.example.studio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.studio.dto.StudioCreateDto;
import org.example.studio.dto.StudioResponseDto;
import org.example.studio.entity.Studio;
import org.example.studio.exception.AccessDeniedException;
import org.example.studio.exception.StudioNotFoundException;
import org.example.studio.repository.StudioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerStudioService {

    private final StudioRepository studioRepo;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    @Transactional
    public StudioResponseDto create(StudioCreateDto dto, UUID userId) {
        log.debug("=== CREATE STUDIO START ===");
        log.debug("User ID: {}", userId);
        log.debug("DTO: name={}, desc={}, avatar={}", dto.getName(), dto.getDescription(), dto.getAvatar());

        Studio studio = new Studio();
        studio.setName(dto.getName());
        studio.setDescription(dto.getDescription());
        studio.setOwnerId(userId);
        studio.getAdminIds().add(userId);
        studio = studioRepo.save(studio);

        meterRegistry.counter("studio.created").increment();
        return toDto(studio);
    }

    @Transactional
    @CacheEvict(allEntries = true, value = {"studio", "studio_posts"})
    public void delete(UUID id, UUID userId) {
        Studio studio = studioRepo.findById(id)
                .orElseThrow(() -> new StudioNotFoundException(id));
        if (!studio.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("Not owner");
        }

        studioRepo.delete(studio);
        meterRegistry.counter("studio.deleted").increment();
    }

    @Transactional
    @CacheEvict(allEntries = true, value = "studio")
    public void removeAdmin(UUID studioId, UUID adminId, UUID requesterId) {
        Studio studio = studioRepo.findById(studioId)
                .orElseThrow(() -> new StudioNotFoundException(studioId));
        if (!studio.getOwnerId().equals(requesterId)) {
            throw new AccessDeniedException("Not owner");
        }

        studio.getAdminIds().remove(adminId);
        studioRepo.save(studio);
        meterRegistry.counter("studio.admin.removed").increment();
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
}