package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.InteractionRequests;
import com.matrimony.backend.dto.response.InteractionResponses.BlockResponse;
import com.matrimony.backend.service.InteractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
@Tag(name = "Blocks")
public class BlockController {
    private final InteractionService service;

    @PostMapping("/{matrimonyId}")
    ApiResponse<BlockResponse> block(@PathVariable String matrimonyId, @Valid @RequestBody(required = false) InteractionRequests.BlockRequest request) {
        return ApiResponse.ok("Profile blocked", service.block(matrimonyId, request));
    }

    @GetMapping
    ApiResponse<PageResponse<BlockResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Blocked profiles", service.blocks(pageable));
    }

    @DeleteMapping("/{matrimonyId}")
    ResponseEntity<Void> unblock(@PathVariable String matrimonyId) {
        service.unblock(matrimonyId);
        return ResponseEntity.noContent().build();
    }
}
