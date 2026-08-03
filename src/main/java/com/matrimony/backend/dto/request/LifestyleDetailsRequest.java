package com.matrimony.backend.dto.request;

public record LifestyleDetailsRequest(
        String diet,
        String smokingHabit,
        String drinkingHabit,
        String hobbies,
        String interests,
        String languagesKnown
) {
}
