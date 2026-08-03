package com.matrimony.backend.dto.request;

import com.matrimony.backend.enums.ProfileVisibility;
import com.matrimony.backend.enums.VisibilityLevel;

public record PrivacySettingRequest(
        ProfileVisibility profileVisibility,
        VisibilityLevel photoVisibility,
        VisibilityLevel contactVisibility,
        VisibilityLevel dateOfBirthVisibility,
        VisibilityLevel incomeVisibility,
        VisibilityLevel horoscopeVisibility,
        Boolean allowProfileViews,
        Boolean allowMessages,
        Boolean allowContactRequests,
        Boolean showOnlineStatus,
        Boolean showLastActive
) {
}
