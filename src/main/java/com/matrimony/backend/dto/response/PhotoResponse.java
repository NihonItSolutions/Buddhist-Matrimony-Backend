package com.matrimony.backend.dto.response;

import com.matrimony.backend.enums.ModerationStatus;
import com.matrimony.backend.enums.PhotoPrivacy;

import java.time.LocalDateTime;

public record PhotoResponse(
        Long id,
        String photoUrl,
        String thumbnailUrl,
        boolean primaryPhoto,
        int displayOrder,
        PhotoPrivacy privacyLevel,
        ModerationStatus moderationStatus,
        String rejectionReason,
        LocalDateTime uploadedAt
) {
}
