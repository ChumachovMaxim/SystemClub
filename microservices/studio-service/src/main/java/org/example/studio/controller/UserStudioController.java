package org.example.studio.controller;

import lombok.RequiredArgsConstructor;
import org.example.studio.dto.*;
import org.example.studio.service.UserStudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/studios")
@RequiredArgsConstructor
public class UserStudioController {

    private final UserStudioService service;

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> follow(@PathVariable UUID id,
                                       @AuthenticationPrincipal UUID userId) {
        service.follow(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollow(@PathVariable UUID id,
                                         @AuthenticationPrincipal UUID userId) {
        service.unfollow(id, userId);
        return ResponseEntity.noContent().build();
    }
}