package org.example.studio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.studio.dto.*;
import org.example.studio.service.OwnerStudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/studios")
@RequiredArgsConstructor
public class OwnerStudioController {

    private final OwnerStudioService service;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<StudioResponseDto> create(@Valid StudioCreateDto dto,
                                                    @AuthenticationPrincipal UUID userId) {
        StudioResponseDto resp = service.create(dto, userId);
        return ResponseEntity.created(URI.create("/studios/" + resp.getId())).body(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal UUID userId) {
        service.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/admins/{adminId}")
    public ResponseEntity<Void> removeAdmin(@PathVariable UUID id,
                                            @PathVariable UUID adminId,
                                            @AuthenticationPrincipal UUID userId) {
        service.removeAdmin(id, adminId, userId);
        return ResponseEntity.noContent().build();
    }
}