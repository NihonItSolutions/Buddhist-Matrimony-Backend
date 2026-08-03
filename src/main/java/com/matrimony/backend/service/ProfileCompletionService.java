package com.matrimony.backend.service;

import com.matrimony.backend.dto.response.ProfileCompletionResponse;
import com.matrimony.backend.entity.MatrimonyProfile;

public interface ProfileCompletionService {
    ProfileCompletionResponse calculate(MatrimonyProfile profile);
}
