package com.matrimony.backend.dto.response;

import com.matrimony.backend.enums.*;

import java.time.LocalDateTime;

public final class InteractionResponses {
    private InteractionResponses() {
    }

    public record InterestResponse(Long id, String senderMatrimonyId, String receiverMatrimonyId, InterestStatus status, String message, LocalDateTime sentAt, LocalDateTime respondedAt) {
    }

    public record BlockResponse(Long id, String blockedMatrimonyId, String reason, LocalDateTime createdAt) {
    }

    public record ReportResponse(Long id, String reportedMatrimonyId, ReportReason reason, ReportStatus status, String description, LocalDateTime createdAt) {
    }

    public record ContactRequestResponse(Long id, String requesterMatrimonyId, String receiverMatrimonyId, ContactRequestStatus status, LocalDateTime requestedAt, LocalDateTime respondedAt) {
    }

    public record ContactDetailsResponse(String matrimonyId, String email, String mobileNumber) {
    }

    public record ConversationResponse(Long id, String profileOneMatrimonyId, String profileTwoMatrimonyId, boolean active, LocalDateTime updatedAt) {
    }

    public record MessageResponse(Long id, Long conversationId, String senderMatrimonyId, String messageText, MessageType messageType, String attachmentUrl, LocalDateTime sentAt, LocalDateTime readAt) {
    }

    public record NotificationResponse(Long id, NotificationType notificationType, String title, String message, String referenceType, Long referenceId, boolean read, LocalDateTime createdAt, LocalDateTime readAt) {
    }
}
