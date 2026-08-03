package com.matrimony.backend;

import com.matrimony.backend.dto.response.ProfileCompletionResponse;
import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.enums.Gender;
import com.matrimony.backend.enums.MaritalStatus;
import com.matrimony.backend.enums.ProfileCreatedFor;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.service.impl.ProfileCompletionServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfileCompletionServiceImplTest {
    @Test
    void identifiesMissingSections() {
        EducationDetailsRepository educationRepository = mock(EducationDetailsRepository.class);
        CareerDetailsRepository careerRepository = mock(CareerDetailsRepository.class);
        FamilyDetailsRepository familyRepository = mock(FamilyDetailsRepository.class);
        PartnerPreferenceRepository preferenceRepository = mock(PartnerPreferenceRepository.class);
        ProfilePhotoRepository photoRepository = mock(ProfilePhotoRepository.class);
        ProfileCompletionServiceImpl service = new ProfileCompletionServiceImpl(
                educationRepository, careerRepository, familyRepository, preferenceRepository, photoRepository
        );

        MatrimonyProfile profile = new MatrimonyProfile();
        profile.setId(1L);
        profile.setProfileCreatedFor(ProfileCreatedFor.MYSELF);
        profile.setFirstName("Amit");
        profile.setGender(Gender.MALE);
        profile.setDateOfBirth(LocalDate.now().minusYears(30));
        profile.setHeightInCm(170);
        profile.setMaritalStatus(MaritalStatus.NEVER_MARRIED);

        when(educationRepository.findByProfileId(1L)).thenReturn(Optional.empty());
        when(careerRepository.findByProfileId(1L)).thenReturn(Optional.empty());
        when(familyRepository.findByProfileId(1L)).thenReturn(Optional.empty());
        when(preferenceRepository.findByProfileId(1L)).thenReturn(Optional.empty());

        ProfileCompletionResponse response = service.calculate(profile);

        assertThat(response.percentage()).isLessThan(50);
        assertThat(response.missingSections()).contains("EDUCATION", "CAREER", "PROFILE_PHOTO");
        assertThat(response.canSubmitForApproval()).isFalse();
    }
}
