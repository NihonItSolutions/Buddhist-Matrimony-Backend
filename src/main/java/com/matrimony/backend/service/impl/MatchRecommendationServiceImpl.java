package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.response.MatchRecommendationResponse;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MatchRecommendationServiceImpl implements com.matrimony.backend.service.MatchRecommendationService {
    private final ProfileMapper mapper;

    @Override
    public MatchRecommendationResponse score(MatrimonyProfile requester, MatrimonyProfile candidate) {
        int score = 20;
        List<String> factors = new ArrayList<>();
        if (Objects.equals(requester.getCity(), candidate.getCity()) && requester.getCity() != null) {
            score += 15;
            factors.add("Preferred city matched");
        }
        if (Objects.equals(requester.getState(), candidate.getState()) && requester.getState() != null) {
            score += 10;
            factors.add("State matched");
        }
        if (Objects.equals(requester.getReligion(), candidate.getReligion()) && requester.getReligion() != null) {
            score += 15;
            factors.add("Religion preference matched");
        }
        if (Objects.equals(requester.getMotherTongue(), candidate.getMotherTongue()) && requester.getMotherTongue() != null) {
            score += 10;
            factors.add("Mother tongue matched");
        }
        if (candidate.getProfileCompleteness() >= 80) {
            score += 15;
            factors.add("Profile is highly complete");
        }
        if (candidate.getLastActiveAt() != null && candidate.getLastActiveAt().isAfter(java.time.LocalDateTime.now().minusDays(15))) {
            score += 10;
            factors.add("Recently active profile");
        }
        if (candidate.getUser().isEmailVerified() || candidate.getUser().isMobileVerified()) {
            score += 5;
            factors.add("Verified profile");
        }
        score = Math.min(score, 100);
        ProfileCardResponse card = new ProfileCardResponse(
                candidate.getUser().getMatrimonyId(),
                mapper.displayName(candidate),
                mapper.age(candidate.getDateOfBirth()),
                candidate.getHeightInCm(),
                candidate.getCity(),
                candidate.getState(),
                null,
                null,
                null,
                false,
                candidate.getUser().isEmailVerified() || candidate.getUser().isMobileVerified(),
                candidate.getLastActiveAt(),
                score,
                false,
                null
        );
        return new MatchRecommendationResponse(card, score, factors);
    }
}
