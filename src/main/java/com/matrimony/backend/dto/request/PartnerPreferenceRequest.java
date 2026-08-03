package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record PartnerPreferenceRequest(
        @Min(18) @Max(100) Integer minimumAge,
        @Min(18) @Max(100) Integer maximumAge,
        @Min(100) @Max(230) Integer minimumHeight,
        @Min(100) @Max(230) Integer maximumHeight,
        List<String> maritalStatuses,
        List<String> religions,
        List<String> communities,
        List<String> subCommunities,
        List<String> motherTongues,
        List<String> countries,
        List<String> states,
        List<String> cities,
        List<String> educationLevels,
        List<String> occupations,
        @DecimalMin("0.0") BigDecimal minimumIncome,
        @DecimalMin("0.0") BigDecimal maximumIncome,
        List<String> dietPreferences,
        List<String> physicalStatusPreferences,
        List<String> manglikPreferences,
        @Size(max = 1200) String description
) {
}
