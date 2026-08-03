package com.matrimony.backend.service;

import com.matrimony.backend.dto.response.MatchRecommendationResponse;
import com.matrimony.backend.entity.MatrimonyProfile;

public interface MatchRecommendationService {
    MatchRecommendationResponse score(MatrimonyProfile requester, MatrimonyProfile candidate);
}
