package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.*;
import com.matrimony.backend.enums.PhotoPrivacy;
import com.matrimony.backend.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profiles")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping
    ResponseEntity<ApiResponse<ProfileDetailsResponse>> create(@Valid @RequestBody ProfileCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Profile created", profileService.create(request)));
    }

    @GetMapping("/me")
    ApiResponse<ProfileDetailsResponse> me() {
        return ApiResponse.ok("Profile", profileService.me());
    }

    @PatchMapping("/me/basic-details")
    ApiResponse<ProfileDetailsResponse> basic(@Valid @RequestBody BasicDetailsRequest request) {
        return ApiResponse.ok("Basic details updated", profileService.updateBasic(request));
    }

    @PatchMapping("/me/personal-details")
    ApiResponse<ProfileDetailsResponse> personal(@Valid @RequestBody PersonalDetailsRequest request) {
        return ApiResponse.ok("Personal details updated", profileService.updatePersonal(request));
    }

    @PatchMapping("/me/religious-details")
    ApiResponse<ProfileDetailsResponse> religious(@Valid @RequestBody ReligiousDetailsRequest request) {
        return ApiResponse.ok("Religious details updated", profileService.updateReligious(request));
    }

    @PatchMapping("/me/location-details")
    ApiResponse<ProfileDetailsResponse> location(@Valid @RequestBody LocationDetailsRequest request) {
        return ApiResponse.ok("Location details updated", profileService.updateLocation(request));
    }

    @PatchMapping("/me/education")
    ApiResponse<ProfileDetailsResponse> education(@Valid @RequestBody EducationDetailsRequest request) {
        return ApiResponse.ok("Education updated", profileService.updateEducation(request));
    }

    @PatchMapping("/me/career")
    ApiResponse<ProfileDetailsResponse> career(@Valid @RequestBody CareerDetailsRequest request) {
        return ApiResponse.ok("Career updated", profileService.updateCareer(request));
    }

    @PatchMapping("/me/family")
    ApiResponse<ProfileDetailsResponse> family(@Valid @RequestBody FamilyDetailsRequest request) {
        return ApiResponse.ok("Family updated", profileService.updateFamily(request));
    }

    @PatchMapping("/me/lifestyle")
    ApiResponse<ProfileDetailsResponse> lifestyle(@Valid @RequestBody LifestyleDetailsRequest request) {
        return ApiResponse.ok("Lifestyle updated", profileService.updateLifestyle(request));
    }

    @PatchMapping("/me/horoscope")
    ApiResponse<ProfileDetailsResponse> horoscope(@Valid @RequestBody HoroscopeDetailsRequest request) {
        return ApiResponse.ok("Horoscope updated", profileService.updateHoroscope(request));
    }

    @PatchMapping("/me/about")
    ApiResponse<ProfileDetailsResponse> about(@Valid @RequestBody AboutRequest request) {
        return ApiResponse.ok("About updated", profileService.updateAbout(request));
    }

    @GetMapping("/me/completion")
    ApiResponse<ProfileCompletionResponse> completion() {
        return ApiResponse.ok("Profile completion", profileService.completion());
    }

    @PostMapping("/me/submit-for-approval")
    ApiResponse<Void> submit() {
        profileService.submitForApproval();
        return ApiResponse.ok("Profile submitted for approval", null);
    }

    @PostMapping("/me/deactivate")
    ApiResponse<Void> deactivate() {
        profileService.deactivate();
        return ApiResponse.ok("Profile deactivated", null);
    }

    @PostMapping("/me/reactivate")
    ApiResponse<Void> reactivate() {
        profileService.reactivate();
        return ApiResponse.ok("Profile reactivated", null);
    }

    @DeleteMapping("/me")
    ResponseEntity<Void> delete() {
        profileService.delete();
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/me/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<PhotoResponse>> upload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Photo uploaded for moderation", profileService.uploadPhoto(file)));
    }

    @GetMapping("/me/photos")
    ApiResponse<List<PhotoResponse>> photos() {
        return ApiResponse.ok("Photos", profileService.photos());
    }

    @PatchMapping("/me/photos/{photoId}/primary")
    ApiResponse<PhotoResponse> primary(@PathVariable Long photoId) {
        return ApiResponse.ok("Primary photo updated", profileService.markPrimary(photoId));
    }

    @PatchMapping("/me/photos/{photoId}/privacy")
    ApiResponse<PhotoResponse> privacy(@PathVariable Long photoId, @RequestParam PhotoPrivacy privacy) {
        return ApiResponse.ok("Photo privacy updated", profileService.updatePhotoPrivacy(photoId, privacy));
    }

    @PatchMapping("/me/photos/reorder")
    ApiResponse<Void> reorder(@RequestBody List<Long> photoIds) {
        profileService.reorderPhotos(photoIds);
        return ApiResponse.ok("Photos reordered", null);
    }

    @DeleteMapping("/me/photos/{photoId}")
    ResponseEntity<Void> deletePhoto(@PathVariable Long photoId) {
        profileService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    ApiResponse<PageResponse<ProfileCardResponse>> search(ProfileSearchRequest request, @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Profile search", profileService.search(request, pageable));
    }

    @GetMapping("/{matrimonyId}")
    ApiResponse<ProfileDetailsResponse> details(@PathVariable String matrimonyId) {
        return ApiResponse.ok("Profile details", profileService.details(matrimonyId));
    }

    @GetMapping("/recommendations")
    ApiResponse<PageResponse<MatchRecommendationResponse>> recommendations(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Recommendations", profileService.recommendations(pageable));
    }

    @GetMapping({"/new-matches", "/recently-active", "/viewed-by-me", "/viewed-my-profile"})
    ApiResponse<PageResponse<ProfileCardResponse>> simplePages(org.springframework.web.context.request.NativeWebRequest request, @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Profiles", profileService.simpleProfilePage("default", pageable));
    }
}
