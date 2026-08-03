package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.response.ProfileCompletionResponse;
import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.enums.ModerationStatus;
import com.matrimony.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileCompletionServiceImpl implements com.matrimony.backend.service.ProfileCompletionService {
    private final EducationDetailsRepository educationRepository;
    private final CareerDetailsRepository careerRepository;
    private final FamilyDetailsRepository familyRepository;
    private final PartnerPreferenceRepository preferenceRepository;
    private final ProfilePhotoRepository photoRepository;

    @Override
    public ProfileCompletionResponse calculate(MatrimonyProfile profile) {
        List<String> missing = new ArrayList<>();
        int completed = 0;
        int total = 9;

        if (StringUtils.hasText(profile.getFirstName()) && profile.getGender() != null && profile.getProfileCreatedFor() != null) completed++; else missing.add("BASIC_DETAILS");
        if (profile.getDateOfBirth() != null && profile.getHeightInCm() != null && profile.getMaritalStatus() != null) completed++; else missing.add("PERSONAL_DETAILS");
        if (StringUtils.hasText(profile.getCity()) && StringUtils.hasText(profile.getState()) && StringUtils.hasText(profile.getCountry())) completed++; else missing.add("LOCATION");
        if (StringUtils.hasText(profile.getReligion()) && StringUtils.hasText(profile.getCommunity()) && StringUtils.hasText(profile.getMotherTongue())) completed++; else missing.add("RELIGION_COMMUNITY");
        if (educationRepository.findByProfileId(profile.getId()).isPresent()) completed++; else missing.add("EDUCATION");
        if (careerRepository.findByProfileId(profile.getId()).isPresent()) completed++; else missing.add("CAREER");
        if (familyRepository.findByProfileId(profile.getId()).isPresent()) completed++; else missing.add("FAMILY_DETAILS");
        if (StringUtils.hasText(profile.getAboutMe()) && profile.getAboutMe().length() >= 40) completed++; else missing.add("ABOUT_SECTION");
        if (preferenceRepository.findByProfileId(profile.getId()).isPresent()) completed++; else missing.add("PARTNER_PREFERENCE");

        boolean hasApprovedPrimaryPhoto = photoRepository.findFirstByProfileIdAndPrimaryPhotoTrueAndModerationStatus(profile.getId(), ModerationStatus.APPROVED).isPresent();
        if (!hasApprovedPrimaryPhoto) {
            missing.add("PROFILE_PHOTO");
        }
        int percentage = (int) Math.round((completed * 100.0) / total);
        return new ProfileCompletionResponse(percentage, missing, percentage >= 80 && hasApprovedPrimaryPhoto);
    }
}
