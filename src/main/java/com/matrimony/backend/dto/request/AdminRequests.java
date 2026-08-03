package com.matrimony.backend.dto.request;

import com.matrimony.backend.enums.Role;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public final class AdminRequests {
    private AdminRequests() {
    }

    public record RoleUpdateRequest(Role role) {
    }

    public record RejectionRequest(@NotBlank String reason) {
    }

    public record MembershipPlanRequest(String name, String code, String description, BigDecimal price, String currency, int durationDays, int dailyInterestLimit, int contactViewLimit, int messageLimit, boolean profileBoostAllowed, boolean assistedService, boolean active) {
    }

    public record StatusRequest(boolean active) {
    }

    public record ReportReviewRequest(String adminComment, boolean suspendProfile) {
    }

    public record ExtendSubscriptionRequest(int days) {
    }

    public record NoteRequest(@NotBlank String note) {
    }
}
