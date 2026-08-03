package com.matrimony.backend.dto.request;

public record EducationDetailsRequest(
        String highestEducation,
        String educationSpecialization,
        String collegeName,
        String additionalQualification
) {
}
