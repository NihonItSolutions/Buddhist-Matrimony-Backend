package com.matrimony.backend.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record HoroscopeDetailsRequest(
        LocalDate dateOfBirth,
        LocalTime timeOfBirth,
        String placeOfBirth,
        String rashi,
        String nakshatra,
        String manglikStatus,
        Boolean horoscopeAvailable,
        String horoscopeDocumentUrl
) {
}
