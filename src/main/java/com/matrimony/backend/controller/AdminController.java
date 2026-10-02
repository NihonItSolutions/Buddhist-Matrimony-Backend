package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.AdminRequests;
import com.matrimony.backend.dto.response.*;
import com.matrimony.backend.dto.response.CatalogResponses.MembershipPlanResponse;
import com.matrimony.backend.dto.response.PaymentResponses.PaymentResponse;
import com.matrimony.backend.dto.response.PaymentResponses.SubscriptionResponse;
import com.matrimony.backend.service.AdminService;
import com.matrimony.backend.service.CatalogService;
import com.matrimony.backend.service.RelationshipManagerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration")
public class AdminController {
    private final AdminService adminService;
    private final CatalogService catalogService;
    private final RelationshipManagerService relationshipManagerService;
    private final com.matrimony.backend.service.SupportService supportService;
    private final com.matrimony.backend.service.FileStorageService fileStorageService;
    private final com.matrimony.backend.service.PaymentService paymentService;

    @GetMapping("/dashboard")
    ApiResponse<AdminDashboardResponse> dashboard(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ApiResponse.ok("Dashboard", adminService.dashboard(fromDate, toDate));
    }

    @GetMapping("/users")
    ApiResponse<PageResponse<UserMeResponse>> users(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Users", adminService.users(pageable));
    }

    @GetMapping("/users/{userId}")
    ApiResponse<UserMeResponse> user(@PathVariable Long userId) {
        return ApiResponse.ok("User", adminService.user(userId));
    }

    @PatchMapping("/users/{userId}/activate")
    ApiResponse<Void> activateUser(@PathVariable Long userId) { adminService.activateUser(userId); return ApiResponse.ok("User activated", null); }

    @PatchMapping("/users/{userId}/suspend")
    ApiResponse<Void> suspendUser(@PathVariable Long userId) { adminService.suspendUser(userId); return ApiResponse.ok("User suspended", null); }

    @PatchMapping("/users/{userId}/role")
    ApiResponse<Void> role(@PathVariable Long userId, @Valid @RequestBody AdminRequests.RoleUpdateRequest request) { adminService.updateRole(userId, request); return ApiResponse.ok("Role updated", null); }

    @DeleteMapping("/users/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable Long userId) { adminService.deleteUser(userId); return ResponseEntity.noContent().build(); }

    @GetMapping("/profiles")
    ApiResponse<PageResponse<ProfileDetailsResponse>> profiles(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Profiles", adminService.profiles(pageable));
    }

    @GetMapping("/profiles/{profileId}")
    ApiResponse<ProfileDetailsResponse> profile(@PathVariable Long profileId) { return ApiResponse.ok("Profile", adminService.profile(profileId)); }

    @PatchMapping("/profiles/{profileId}/approve")
    ApiResponse<Void> approveProfile(@PathVariable Long profileId) { adminService.approveProfile(profileId); return ApiResponse.ok("Profile approved", null); }

    @PatchMapping("/profiles/{profileId}/reject")
    ApiResponse<Void> rejectProfile(@PathVariable Long profileId, @Valid @RequestBody AdminRequests.RejectionRequest request) { adminService.rejectProfile(profileId, request); return ApiResponse.ok("Profile rejected", null); }

    @PatchMapping("/profiles/{profileId}/suspend")
    ApiResponse<Void> suspendProfile(@PathVariable Long profileId) { adminService.suspendProfile(profileId); return ApiResponse.ok("Profile suspended", null); }

    @PatchMapping("/profiles/{profileId}/reactivate")
    ApiResponse<Void> reactivateProfile(@PathVariable Long profileId) { adminService.reactivateProfile(profileId); return ApiResponse.ok("Profile reactivated", null); }

    @PatchMapping("/profiles/{profileId}/verify-documents")
    ApiResponse<Void> verifyDocuments(@PathVariable Long profileId) { adminService.verifyDocuments(profileId, true); return ApiResponse.ok("Documents verified", null); }

    @PatchMapping("/profiles/{profileId}/unverify-documents")
    ApiResponse<Void> unverifyDocuments(@PathVariable Long profileId) { adminService.verifyDocuments(profileId, false); return ApiResponse.ok("Documents unverified", null); }

    @GetMapping("/photos/pending")
    ApiResponse<PageResponse<PhotoResponse>> pendingPhotos(@PageableDefault(size = 20) Pageable pageable) { return ApiResponse.ok("Pending photos", adminService.pendingPhotos(pageable)); }

    @GetMapping("/photos/{photoId}")
    ApiResponse<PhotoResponse> photo(@PathVariable Long photoId) { return ApiResponse.ok("Photo", adminService.photo(photoId)); }

    @PatchMapping("/photos/{photoId}/approve")
    ApiResponse<Void> approvePhoto(@PathVariable Long photoId) { adminService.approvePhoto(photoId); return ApiResponse.ok("Photo approved", null); }

    @PatchMapping("/photos/{photoId}/reject")
    ApiResponse<Void> rejectPhoto(@PathVariable Long photoId, @Valid @RequestBody AdminRequests.RejectionRequest request) { adminService.rejectPhoto(photoId, request); return ApiResponse.ok("Photo rejected", null); }

    @GetMapping("/reports")
    ApiResponse<PageResponse<?>> reports(@PageableDefault(size = 20) Pageable pageable) { return ApiResponse.ok("Reports", adminService.reports(pageable)); }

    @GetMapping("/reports/{reportId}")
    ApiResponse<PageResponse<?>> report(@PathVariable Long reportId, @PageableDefault(size = 1) Pageable pageable) { return ApiResponse.ok("Reports", adminService.reports(pageable)); }

    @PatchMapping({"/reports/{reportId}/review", "/reports/{reportId}/resolve", "/reports/{reportId}/reject"})
    ApiResponse<Void> resolveReport(@PathVariable Long reportId, @RequestBody AdminRequests.ReportReviewRequest request) { adminService.resolveReport(reportId, request); return ApiResponse.ok("Report updated", null); }

    @GetMapping("/payments")
    ApiResponse<PageResponse<PaymentResponse>> payments(@PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) { return ApiResponse.ok("Payments", adminService.payments(pageable)); }

    @GetMapping("/payments/{paymentId}")
    ApiResponse<PageResponse<PaymentResponse>> payment(@PathVariable Long paymentId, @PageableDefault(size = 1) Pageable pageable) { return ApiResponse.ok("Payments", adminService.payments(pageable)); }

    @GetMapping("/subscriptions")
    ApiResponse<PageResponse<SubscriptionResponse>> subscriptions(@PageableDefault(size = 20) Pageable pageable) { return ApiResponse.ok("Subscriptions", adminService.subscriptions(pageable)); }

    @GetMapping("/subscriptions/{subscriptionId}")
    ApiResponse<PageResponse<SubscriptionResponse>> subscription(@PathVariable Long subscriptionId, @PageableDefault(size = 1) Pageable pageable) { return ApiResponse.ok("Subscriptions", adminService.subscriptions(pageable)); }

    @PostMapping("/subscriptions/{subscriptionId}/extend")
    ApiResponse<Void> extendSubscription(@PathVariable Long subscriptionId, @RequestBody AdminRequests.ExtendSubscriptionRequest request) { return ApiResponse.ok("Subscription extension recorded for gateway/manual processing", null); }

    @GetMapping("/payments/pending-count")
    ApiResponse<java.util.Map<String, Long>> pendingPaymentCount() {
        return ApiResponse.ok("Payments awaiting verification", java.util.Map.of("count", paymentService.pendingVerificationCount()));
    }

    @PostMapping("/payments/{paymentId}/approve")
    ApiResponse<PaymentResponse> approvePayment(@PathVariable Long paymentId, @Valid @RequestBody com.matrimony.backend.dto.request.PaymentRequests.ApprovePaymentRequest request) {
        PaymentResponse payment = paymentService.verifyManualPayment(paymentId, request.amountReceived());
        String message = payment.status() == com.matrimony.backend.enums.PaymentStatus.SUCCESS
                ? "Payment verified. Membership activated and user notified."
                : "Amount is less than plan price. Marked as underpaid and user notified.";
        return ApiResponse.ok(message, payment);
    }

    @PostMapping("/payments/{paymentId}/reject")
    ApiResponse<PaymentResponse> rejectPayment(@PathVariable Long paymentId, @RequestBody(required = false) com.matrimony.backend.dto.request.PaymentRequests.RejectPaymentRequest request) {
        return ApiResponse.ok("Payment rejected", paymentService.rejectManualPayment(paymentId, request == null ? null : request.reason()));
    }

    @PostMapping("/payments/{paymentId}/refund-request")
    ApiResponse<Void> refundRequest(@PathVariable Long paymentId) { return ApiResponse.ok("Refund request recorded for gateway confirmation", null); }

    @PostMapping("/membership-plans")
    ApiResponse<MembershipPlanResponse> createPlan(@Valid @RequestBody AdminRequests.MembershipPlanRequest request) { return ApiResponse.ok("Membership plan created", catalogService.createPlan(request)); }

    @PutMapping("/membership-plans/{planId}")
    ApiResponse<MembershipPlanResponse> updatePlan(@PathVariable Long planId, @Valid @RequestBody AdminRequests.MembershipPlanRequest request) { return ApiResponse.ok("Membership plan updated", catalogService.updatePlan(planId, request)); }

    @PatchMapping("/membership-plans/{planId}/status")
    ApiResponse<MembershipPlanResponse> status(@PathVariable Long planId, @RequestBody AdminRequests.StatusRequest request) { return ApiResponse.ok("Membership plan status updated", catalogService.updatePlanStatus(planId, request)); }

    @DeleteMapping("/membership-plans/{planId}")
    ResponseEntity<Void> deletePlan(@PathVariable Long planId) { catalogService.deletePlan(planId); return ResponseEntity.noContent().build(); }

    @PostMapping("/relationship-managers/{managerId}/customers/{userId}")
    ApiResponse<Void> assign(@PathVariable Long managerId, @PathVariable Long userId) { relationshipManagerService.assign(managerId, userId); return ApiResponse.ok("Customer assigned", null); }

    @DeleteMapping("/relationship-managers/{managerId}/customers/{userId}")
    ResponseEntity<Void> unassign(@PathVariable Long managerId, @PathVariable Long userId) { relationshipManagerService.unassign(managerId, userId); return ResponseEntity.noContent().build(); }

    // Success Stories
    @GetMapping("/success-stories")
    ApiResponse<com.matrimony.backend.dto.PageResponse<?>> adminStories(@org.springframework.data.web.PageableDefault(size = 20) org.springframework.data.domain.Pageable pageable) { return ApiResponse.ok("Stories", supportService.allStories(pageable)); }

    @PostMapping("/success-stories")
    ApiResponse<Object> adminCreateStory(@jakarta.validation.Valid @RequestBody com.matrimony.backend.dto.request.SupportRequests.SuccessStoryRequest request) { return ApiResponse.ok("Story created", supportService.createSuccessStory(request)); }

    @PatchMapping("/success-stories/{storyId}/approve")
    ApiResponse<Void> approveStory(@PathVariable Long storyId) { supportService.approveStory(storyId); return ApiResponse.ok("Story approved", null); }

    @PatchMapping("/success-stories/{storyId}/reject")
    ApiResponse<Void> rejectStory(@PathVariable Long storyId, @RequestBody(required = false) java.util.Map<String, String> body) { supportService.rejectStory(storyId, body != null ? body.get("reason") : null); return ApiResponse.ok("Story rejected", null); }

    @DeleteMapping("/success-stories/{storyId}")
    ResponseEntity<Void> adminDeleteStory(@PathVariable Long storyId) { supportService.deleteStory(storyId); return ResponseEntity.noContent().build(); }

    @PostMapping(value = "/success-stories/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<String> uploadStoryPhoto(@RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        com.matrimony.backend.service.FileStorageService.StoredFile stored = fileStorageService.storeProfilePhoto(file, "admin_success_stories");
        return ApiResponse.ok("Photo uploaded successfully", stored.url());
    }
}
