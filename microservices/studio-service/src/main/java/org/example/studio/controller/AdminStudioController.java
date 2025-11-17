package org.example.studio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.studio.dto.*;
import org.example.studio.service.AdminStudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/studios")
@RequiredArgsConstructor
public class AdminStudioController {

    private final AdminStudioService service;

    @PostMapping(value = "/{id}/posts", consumes = "multipart/form-data")
    public PostResponseDto addPost(@PathVariable UUID id,
                                   @Valid PostCreateDto dto,
                                   @AuthenticationPrincipal UUID userId) {
        return service.addPost(id, dto, userId);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public StudioResponseDto update(@PathVariable UUID id,
                                    @Valid StudioUpdateDto dto,
                                    @AuthenticationPrincipal UUID userId) {
        return service.update(id, dto, userId);
    }

    @PostMapping("/{id}/admins")
    public ResponseEntity<Void> addAdmin(@PathVariable UUID id,
                                         @RequestParam UUID adminId,
                                         @AuthenticationPrincipal UUID userId) {
        service.addAdmin(id, adminId, userId);
        return ResponseEntity.ok().build();
    }
}