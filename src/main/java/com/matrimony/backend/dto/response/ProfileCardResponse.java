package com.matrimony.backend.dto.response;

import java.time.LocalDateTime;

public record ProfileCardResponse(
        String matrimonyId,
        String displayName,
        Integer age,
        Integer heightInCm,
        String city,
        String state,
        String education,
        String occupation,
        String primaryPhotoUrl,
        boolean photoVisible,
        boolean profileVerified,
        boolean documentsVerified,
        LocalDateTime lastActiveAt,
        int matchScore,
        boolean shortlisted,
        String interestStatus,
        String gender
) {
}
