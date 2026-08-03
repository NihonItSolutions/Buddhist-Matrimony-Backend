package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.request.PrivacySettingRequest;
import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.entity.ProfilePrivacySetting;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.repository.MatrimonyProfileRepository;
import com.matrimony.backend.repository.ProfilePrivacySettingRepository;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.PrivacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrivacyServiceImpl implements PrivacyService {
    private final CurrentUser currentUser;
    private final MatrimonyProfileRepository profileRepository;
    private final ProfilePrivacySettingRepository repository;

    @Override
    @Transactional(readOnly = true)
    public PrivacySettingRequest me() {
        return toRequest(setting());
    }

    @Override
    @Transactional
    public PrivacySettingRequest update(PrivacySettingRequest request) {
        ProfilePrivacySetting setting = setting();
        if (request.profileVisibility() != null) setting.setProfileVisibility(request.profileVisibility());
        if (request.photoVisibility() != null) setting.setPhotoVisibility(request.photoVisibility());
        if (request.contactVisibility() != null) setting.setContactVisibility(request.contactVisibility());
        if (request.dateOfBirthVisibility() != null) setting.setDateOfBirthVisibility(request.dateOfBirthVisibility());
        if (request.incomeVisibility() != null) setting.setIncomeVisibility(request.incomeVisibility());
        if (request.horoscopeVisibility() != null) setting.setHoroscopeVisibility(request.horoscopeVisibility());
        if (request.allowProfileViews() != null) setting.setAllowProfileViews(request.allowProfileViews());
        if (request.allowMessages() != null) setting.setAllowMessages(request.allowMessages());
        if (request.allowContactRequests() != null) setting.setAllowContactRequests(request.allowContactRequests());
        if (request.showOnlineStatus() != null) setting.setShowOnlineStatus(request.showOnlineStatus());
        if (request.showLastActive() != null) setting.setShowLastActive(request.showLastActive());
        return toRequest(setting);
    }

    private ProfilePrivacySetting setting() {
        MatrimonyProfile profile = profileRepository.findByUser(currentUser.get()).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return repository.findByProfileId(profile.getId()).orElseGet(() -> {
            ProfilePrivacySetting created = new ProfilePrivacySetting();
            created.setProfile(profile);
            return repository.save(created);
        });
    }

    private PrivacySettingRequest toRequest(ProfilePrivacySetting setting) {
        return new PrivacySettingRequest(setting.getProfileVisibility(), setting.getPhotoVisibility(), setting.getContactVisibility(), setting.getDateOfBirthVisibility(), setting.getIncomeVisibility(), setting.getHoroscopeVisibility(), setting.isAllowProfileViews(), setting.isAllowMessages(), setting.isAllowContactRequests(), setting.isShowOnlineStatus(), setting.isShowLastActive());
    }
}
