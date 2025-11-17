package org.example.studio.service;

import lombok.RequiredArgsConstructor;
import org.example.studio.dto.PostResponseDto;
import org.example.studio.dto.StudioResponseDto;
import org.example.studio.entity.Post;
import org.example.studio.entity.Studio;
import org.example.studio.exception.StudioNotFoundException;
import org.example.studio.repository.PostRepository;
import org.example.studio.repository.StudioRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicStudioService {

    private final StudioRepository studioRepo;
    private final PostRepository postRepo;

    @Cacheable(value = "studio", key = "#id")
    @Transactional(readOnly = true)
    public StudioResponseDto get(UUID id) {
        return studioRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new StudioNotFoundException(id));
    }

    @Cacheable(value = "studio_posts", key = "#id")
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts(UUID id) {
        return postRepo.findByStudioIdOrderByTimestampDesc(id).stream()
                .map(this::toPostDto)
                .collect(Collectors.toList());
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