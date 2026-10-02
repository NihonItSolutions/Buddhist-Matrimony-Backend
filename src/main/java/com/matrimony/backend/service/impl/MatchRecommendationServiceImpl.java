package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.response.MatchRecommendationResponse;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.entity.CareerDetails;
import com.matrimony.backend.entity.EducationDetails;
import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.entity.ProfilePhoto;
import com.matrimony.backend.enums.InterestStatus;
import com.matrimony.backend.mapper.ProfileMapper;
import com.matrimony.backend.repository.CareerDetailsRepository;
import com.matrimony.backend.repository.EducationDetailsRepository;
import com.matrimony.backend.repository.InterestRepository;
import com.matrimony.backend.repository.ProfilePhotoRepository;
import com.matrimony.backend.repository.ShortlistedProfileRepository;
import com.matrimony.backend.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MatchRecommendationServiceImpl implements com.matrimony.backend.service.MatchRecommendationService {
    private final ProfileMapper mapper;
    private final EducationDetailsRepository educationRepository;
    private final CareerDetailsRepository careerRepository;
    private final ProfilePhotoRepository photoRepository;
    private final ShortlistedProfileRepository shortlistRepository;
    private final InterestRepository interestRepository;
    private final EntitlementService entitlementService;

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
        EducationDetails education = educationRepository.findByProfileId(candidate.getId()).orElse(null);
        CareerDetails career = careerRepository.findByProfileId(candidate.getId()).orElse(null);
        ProfilePhoto photo = photoRepository.findDisplayPhoto(candidate.getId()).orElse(null);
        boolean shortlisted = shortlistRepository.existsByOwnerProfileIdAndShortlistedProfileId(requester.getId(), candidate.getId());
        String interestStatus = interestRepository.findActiveBetween(requester.getId(), candidate.getId(), List.of(InterestStatus.PENDING, InterestStatus.ACCEPTED))
                .map(interest -> interest.getStatus().name())
                .orElse(null);
        boolean hasActivePlan = entitlementService.hasActivePlan(requester.getUser());
        ProfileCardResponse card = mapper.toCard(candidate, education, career, photo, shortlisted, interestStatus, score, hasActivePlan);
        return new MatchRecommendationResponse(card, score, factors);
    }
}
