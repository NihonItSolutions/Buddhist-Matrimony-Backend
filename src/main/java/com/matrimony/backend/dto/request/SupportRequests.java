package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class SupportRequests {
    private SupportRequests() {
    }

    public record TicketRequest(String category, @NotBlank String subject, @NotBlank @Size(max = 3000) String description) {
    }

    public record TicketMessageRequest(@NotBlank @Size(max = 3000) String message) {
    }

    public record FeedbackRequest(@NotBlank @Size(max = 3000) String message) {
    }

    public record ContactUsRequest(@NotBlank String name, @NotBlank String email, @NotBlank @Size(max = 3000) String message) {
    }

    public record SuccessStoryRequest(String brideMatrimonyId, String groomMatrimonyId, String brideName, String groomName, @NotBlank @Size(max = 3000) String story, java.time.LocalDate marriageDate, String photoUrl) {
    }
}
