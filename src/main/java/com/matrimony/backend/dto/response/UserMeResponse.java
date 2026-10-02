package com.matrimony.backend.dto.response;

import com.matrimony.backend.enums.AccountStatus;
import com.matrimony.backend.enums.Role;

public record UserMeResponse(
        Long id,
        String matrimonyId,
        String email,
        String mobileNumber,
        Role role,
        AccountStatus accountStatus,
        boolean emailVerified,
        boolean mobileVerified,
        int profileCompletion,
        boolean hasActiveSubscription
) {
}
