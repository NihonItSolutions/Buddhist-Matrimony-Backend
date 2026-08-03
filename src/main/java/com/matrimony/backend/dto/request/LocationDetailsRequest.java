package com.matrimony.backend.dto.request;

public record LocationDetailsRequest(
        String citizenship,
        String country,
        String state,
        String district,
        String city,
        String postalCode,
        String residencyStatus
) {
}
