package com.matrimony.backend.dto.response;

import java.util.List;

public record MatchRecommendationResponse(
        ProfileCardResponse profile,
        int matchScore,
        List<String> matchingFactors
) {
}
