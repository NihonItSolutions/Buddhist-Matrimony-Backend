package com.matrimony.backend.dto.request;

import com.matrimony.backend.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @StrongPassword String newPassword
) {
}
