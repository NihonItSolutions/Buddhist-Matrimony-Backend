package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CareerDetailsRequest(
        String employedIn,
        String occupation,
        String companyName,
        String jobTitle,
        @DecimalMin(value = "0.0") BigDecimal annualIncome,
        String incomeCurrency,
        String workCountry,
        String workState,
        String workCity
) {
}
