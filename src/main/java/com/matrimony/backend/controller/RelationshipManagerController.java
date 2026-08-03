package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.request.AdminRequests;
import com.matrimony.backend.service.RelationshipManagerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationship-manager")
@RequiredArgsConstructor
@Tag(name = "Relationship Manager")
public class RelationshipManagerController {
    private final RelationshipManagerService service;

    @GetMapping("/customers")
    ApiResponse<List<?>> customers() { return ApiResponse.ok("Assigned customers", service.customers()); }

    @GetMapping("/customers/{userId}")
    ApiResponse<Object> customer(@PathVariable Long userId) { return ApiResponse.ok("Customer", service.customer(userId)); }

    @PostMapping("/customers/{userId}/notes")
    ApiResponse<Object> note(@PathVariable Long userId, @Valid @RequestBody AdminRequests.NoteRequest request) { return ApiResponse.ok("Note added", service.addNote(userId, request)); }

    @GetMapping("/customers/{userId}/notes")
    ApiResponse<List<?>> notes(@PathVariable Long userId) { return ApiResponse.ok("Notes", service.notes(userId)); }

    @PostMapping("/customers/{userId}/suggestions/{profileId}")
    ApiResponse<Object> suggest(@PathVariable Long userId, @PathVariable Long profileId) { return ApiResponse.ok("Suggestion added", service.suggest(userId, profileId)); }

    @GetMapping("/customers/{userId}/suggestions")
    ApiResponse<List<?>> suggestions(@PathVariable Long userId) { return ApiResponse.ok("Suggestions", service.suggestions(userId)); }
}
