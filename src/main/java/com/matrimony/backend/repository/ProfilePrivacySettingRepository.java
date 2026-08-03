package com.matrimony.backend.repository;

import com.matrimony.backend.entity.ProfilePrivacySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePrivacySettingRepository extends JpaRepository<ProfilePrivacySetting, Long> {
    Optional<ProfilePrivacySetting> findByProfileId(Long profileId);
}
