package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.request.PartnerPreferenceRequest;
import com.matrimony.backend.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partner-preferences")
@RequiredArgsConstructor
@Tag(name = "Partner Preferences")
public class PartnerPreferenceController {
    private final ProfileService profileService;

    @GetMapping("/me")
    ApiResponse<PartnerPreferenceRequest> me() {
        return ApiResponse.ok("Partner preference", profileService.getPreference());
    }

    @PutMapping("/me")
    ApiResponse<PartnerPreferenceRequest> update(@Valid @RequestBody PartnerPreferenceRequest request) {
        return ApiResponse.ok("Partner preference updated", profileService.updatePreference(request));
    }
}
