package com.matrimony.backend.dto.request;

import com.matrimony.backend.enums.MaritalStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PersonalDetailsRequest(
        @NotNull LocalDate dateOfBirth,
        @Min(100) @Max(230) Integer heightInCm,
        @Min(25) @Max(250) Integer weightInKg,
        String bloodGroup,
        MaritalStatus maritalStatus,
        @Min(0) Integer numberOfChildren,
        String childrenLivingStatus,
        String physicalStatus,
        String leavingCertificateUrl,
        String aadharCardUrl
) {
}
