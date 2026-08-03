package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.request.PrivacySettingRequest;
import com.matrimony.backend.service.PrivacyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/privacy-settings")
@RequiredArgsConstructor
@Tag(name = "Privacy")
public class PrivacyController {
    private final PrivacyService service;

    @GetMapping("/me")
    ApiResponse<PrivacySettingRequest> me() {
        return ApiResponse.ok("Privacy settings", service.me());
    }

    @PutMapping("/me")
    ApiResponse<PrivacySettingRequest> update(@RequestBody PrivacySettingRequest request) {
        return ApiResponse.ok("Privacy settings updated", service.update(request));
    }
}
