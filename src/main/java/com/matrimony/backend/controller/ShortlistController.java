package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.service.InteractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shortlists")
@RequiredArgsConstructor
@Tag(name = "Shortlists")
public class ShortlistController {
    private final InteractionService service;

    @PostMapping("/{matrimonyId}")
    ApiResponse<Void> add(@PathVariable String matrimonyId) {
        service.shortlist(matrimonyId);
        return ApiResponse.ok("Profile shortlisted", null);
    }

    @GetMapping
    ApiResponse<PageResponse<ProfileCardResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Shortlists", service.shortlists(pageable));
    }

    @DeleteMapping("/{matrimonyId}")
    ResponseEntity<Void> remove(@PathVariable String matrimonyId) {
        service.removeShortlist(matrimonyId);
        return ResponseEntity.noContent().build();
    }
}
