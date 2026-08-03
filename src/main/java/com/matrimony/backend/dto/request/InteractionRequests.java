package com.matrimony.backend.dto.request;

import com.matrimony.backend.enums.ReportReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class InteractionRequests {
    private InteractionRequests() {
    }

    public record InterestMessage(@Size(max = 500) String message) {
    }

    public record BlockRequest(@Size(max = 500) String reason) {
    }

    public record ReportRequest(@NotNull ReportReason reason, @Size(max = 1200) String description) {
    }

    public record MessageRequest(@NotBlank @Size(max = 2000) String messageText) {
    }

    public record ContactRequestBody(String note) {
    }
}
