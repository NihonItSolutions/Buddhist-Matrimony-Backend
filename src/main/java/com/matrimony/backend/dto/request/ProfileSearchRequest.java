package com.matrimony.backend.dto.request;

import com.matrimony.backend.enums.Gender;
import com.matrimony.backend.enums.MaritalStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

public record ProfileSearchRequest(
        String matrimonyId,
        Gender gender,
        Integer minimumAge,
        Integer maximumAge,
        Integer minimumHeight,
        Integer maximumHeight,
        MaritalStatus maritalStatus,
        String religion,
        String community,
        String subCommunity,
        String motherTongue,
        String country,
        String state,
        String district,
        String city,
        String education,
        String occupation,
        BigDecimal minimumIncome,
        BigDecimal maximumIncome,
        String diet,
        String physicalStatus,
        String manglikStatus,
        Boolean hasPhoto,
        Integer lastActiveWithinDays,
        String keyword,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime registeredFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime registeredTo
) {
}
