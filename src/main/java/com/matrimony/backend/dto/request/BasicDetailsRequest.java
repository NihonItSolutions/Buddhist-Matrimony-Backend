package com.matrimony.backend.dto.request;

import com.matrimony.backend.enums.Gender;
import com.matrimony.backend.enums.ProfileCreatedFor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BasicDetailsRequest(
        @NotNull ProfileCreatedFor profileCreatedFor,
        @NotBlank @Size(max = 80) String firstName,
        @Size(max = 80) String middleName,
        @NotBlank @Size(max = 80) String lastName,
        @NotNull Gender gender
) {
}
