package org.example.studio.service;

import lombok.RequiredArgsConstructor;
import org.example.studio.entity.StudioFollower;
import org.example.studio.repository.StudioFollowerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStudioService {

    private final StudioFollowerRepository followerRepo;
    private final io.micrometer.core.instrument.MeterRegistry meterRegistry;

    @Transactional
    @CacheEvict(value = "studio", key = "#studioId")
    public void follow(UUID studioId, UUID userId) {
        if (followerRepo.existsByStudioIdAndUserId(studioId, userId)) {
            return;
        }
        StudioFollower follower = new StudioFollower();
        follower.setStudioId(studioId);
        follower.setUserId(userId);
        followerRepo.save(follower);
        meterRegistry.counter("studio.followed").increment();
    }

    @Transactional
    @CacheEvict(value = "studio", key = "#studioId")
    public void unfollow(UUID studioId, UUID userId) {
        if (!followerRepo.existsByStudioIdAndUserId(studioId, userId)) {
            return;
        }
        followerRepo.deleteByStudioIdAndUserId(studioId, userId);
        meterRegistry.counter("studio.unfollowed").increment();
    }
}