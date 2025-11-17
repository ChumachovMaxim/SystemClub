package org.example.studio.controller;

import lombok.RequiredArgsConstructor;
import org.example.studio.dto.*;
import org.example.studio.service.PublicStudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/studios")
@RequiredArgsConstructor
public class StudioController {

    private final PublicStudioService service;

    @GetMapping("/{id}")
    public StudioResponseDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping("/{id}/posts")
    public List<PostResponseDto> getPosts(@PathVariable UUID id) {
        return service.getPosts(id);
    }
}