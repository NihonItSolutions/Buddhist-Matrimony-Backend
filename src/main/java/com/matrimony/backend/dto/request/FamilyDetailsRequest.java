package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record FamilyDetailsRequest(
        String familyType,
        String familyStatus,
        String familyValues,
        String fatherStatus,
        String fatherOccupation,
        String motherStatus,
        String motherOccupation,
        @Min(0) Integer numberOfBrothers,
        @Min(0) Integer marriedBrothers,
        @Min(0) Integer numberOfSisters,
        @Min(0) Integer marriedSisters,
        String familyCountry,
        String familyState,
        String familyCity,
        @Size(max = 1200) String familyDescription
) {
}
