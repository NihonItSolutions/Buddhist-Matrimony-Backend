package com.matrimony.backend.service;

import com.matrimony.backend.dto.request.PrivacySettingRequest;

public interface PrivacyService {
    PrivacySettingRequest me();

    PrivacySettingRequest update(PrivacySettingRequest request);
}
