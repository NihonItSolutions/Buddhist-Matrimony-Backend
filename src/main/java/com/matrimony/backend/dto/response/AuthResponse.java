package com.matrimony.backend.dto.response;

import com.matrimony.backend.enums.AccountStatus;
import com.matrimony.backend.enums.Role;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String matrimonyId,
        Role role,
        AccountStatus accountStatus,
        int profileCompletion
) {
}
