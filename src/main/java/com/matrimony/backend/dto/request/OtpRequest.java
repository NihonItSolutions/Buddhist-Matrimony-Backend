package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OtpRequest(@NotBlank String destination) {
}
