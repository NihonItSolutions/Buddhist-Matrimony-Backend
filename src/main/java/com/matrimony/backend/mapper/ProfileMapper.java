package com.matrimony.backend.mapper;

import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.PhotoResponse;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.dto.response.ProfileDetailsResponse;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.ModerationStatus;
import com.matrimony.backend.enums.ProfileStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class ProfileMapper {
    public Integer age(LocalDate dob) {
        return dob == null ? null : Period.between(dob, LocalDate.now()).getYears();
    }

    public String displayName(MatrimonyProfile profile, boolean hasActivePlan) {
        String surname = (profile.getLastName() != null && !profile.getLastName().isBlank())
                ? profile.getLastName()
                : null;
        if (!hasActivePlan) {
            // No plan: show *** Surname (e.g., "*** Kamble")
            return surname != null ? "*** " + surname : "***";
        }
        // With plan: show *** Surname (first name always hidden for privacy)
        return surname != null ? "*** " + surname : "***";
    }

    public ProfileCardResponse toCard(MatrimonyProfile profile,
                                      EducationDetails education,
                                      CareerDetails career,
                                      ProfilePhoto primaryPhoto,
                                      boolean shortlisted,
                                      String interestStatus,
                                      int matchScore,
                                      boolean hasActivePlan) {
        boolean visiblePhoto = primaryPhoto != null && primaryPhoto.getModerationStatus() != ModerationStatus.REJECTED;
        return new ProfileCardResponse(
                profile.getUser().getMatrimonyId(),
                displayName(profile, hasActivePlan),
                age(profile.getDateOfBirth()),
                profile.getHeightInCm(),
                profile.getCity(),
                profile.getState(),
                education == null ? null : education.getHighestEducation(),
                career == null ? null : career.getOccupation(),
                visiblePhoto ? primaryPhoto.getPhotoUrl() : null,
                visiblePhoto,
                profile.getUser().isEmailVerified() || profile.getUser().isMobileVerified(),
                profile.isDocumentsVerified(),
                profile.getLastActiveAt(),
                matchScore,
                shortlisted,
                interestStatus,
                profile.getGender() != null ? profile.getGender().name() : null
        );
    }

    public ProfileDetailsResponse toDetails(MatrimonyProfile profile,
                                            EducationDetails education,
                                            CareerDetails career,
                                            FamilyDetails family,
                                            LifestyleDetails lifestyle,
                                            HoroscopeDetails horoscope,
                                            PartnerPreference preference,
                                            List<ProfilePhoto> photos,
                                            boolean canSeeDob,
                                            boolean canSeePrivatePhoto,
                                            boolean shortlisted,
                                            String interestStatus,
                                            String contactRequestStatus,
                                            boolean hasActivePlan) {
        List<PhotoResponse> visiblePhotos = photos.stream()
                .filter(photo -> canSeePrivatePhoto || photo.getModerationStatus() != ModerationStatus.REJECTED)
                .map(this::toPhoto)
                .toList();
        return new ProfileDetailsResponse(
                profile.getUser().getMatrimonyId(),
                profile.getProfileCreatedFor(),
                hasActivePlan ? profile.getFirstName() : "****",
                profile.getMiddleName(),
                profile.getLastName(),
                profile.getGender(),
                canSeeDob ? profile.getDateOfBirth() : null,
                age(profile.getDateOfBirth()),
                profile.getHeightInCm(),
                profile.getWeightInKg(),
                profile.getBloodGroup(),
                profile.getMaritalStatus(),
                profile.getNumberOfChildren(),
                profile.getChildrenLivingStatus(),
                profile.getPhysicalStatus(),
                profile.getLeavingCertificateUrl(),
                profile.getAadharCardUrl(),
                profile.getMotherTongue(),
                profile.getReligion(),
                profile.getCommunity(),
                profile.getSubCommunity(),
                profile.getCasteNoBar(),
                profile.getGothra(),
                profile.getManglikStatus(),
                profile.getCitizenship(),
                profile.getCountry(),
                profile.getState(),
                profile.getDistrict(),
                profile.getCity(),
                profile.getPostalCode(),
                profile.getResidencyStatus(),
                profile.getAboutMe(),
                profile.getProfileStatus(),
                profile.getProfileCompleteness(),
                profile.getLastActiveAt(),
                education == null ? null : new EducationDetailsRequest(education.getHighestEducation(), education.getEducationSpecialization(), education.getCollegeName(), education.getAdditionalQualification()),
                career == null ? null : new CareerDetailsRequest(career.getEmployedIn(), career.getOccupation(), career.getCompanyName(), career.getJobTitle(), career.getAnnualIncome(), career.getIncomeCurrency(), career.getWorkCountry(), career.getWorkState(), career.getWorkCity()),
                family == null ? null : new FamilyDetailsRequest(family.getFamilyType(), family.getFamilyStatus(), family.getFamilyValues(), family.getFatherStatus(), family.getFatherOccupation(), family.getMotherStatus(), family.getMotherOccupation(), family.getNumberOfBrothers(), family.getMarriedBrothers(), family.getNumberOfSisters(), family.getMarriedSisters(), family.getFamilyCountry(), family.getFamilyState(), family.getFamilyCity(), family.getFamilyDescription()),
                lifestyle == null ? null : new LifestyleDetailsRequest(lifestyle.getDiet(), lifestyle.getSmokingHabit(), lifestyle.getDrinkingHabit(), lifestyle.getHobbies(), lifestyle.getInterests(), lifestyle.getLanguagesKnown()),
                horoscope == null ? null : new HoroscopeDetailsRequest(horoscope.getDateOfBirth(), horoscope.getTimeOfBirth(), horoscope.getPlaceOfBirth(), horoscope.getRashi(), horoscope.getNakshatra(), horoscope.getManglikStatus(), horoscope.getHoroscopeAvailable(), horoscope.getHoroscopeDocumentUrl()),
                preference == null ? null : new PartnerPreferenceRequest(preference.getMinimumAge(), preference.getMaximumAge(), preference.getMinimumHeight(), preference.getMaximumHeight(), copy(preference.getMaritalStatuses()), copy(preference.getReligions()), copy(preference.getCommunities()), copy(preference.getSubCommunities()), copy(preference.getMotherTongues()), copy(preference.getCountries()), copy(preference.getStates()), copy(preference.getCities()), copy(preference.getEducationLevels()), copy(preference.getOccupations()), preference.getMinimumIncome(), preference.getMaximumIncome(), copy(preference.getDietPreferences()), copy(preference.getPhysicalStatusPreferences()), copy(preference.getManglikPreferences()), preference.getDescription()),
                visiblePhotos,
                shortlisted,
                interestStatus,
                contactRequestStatus
        );
    }

    private List<String> copy(List<String> values) {
        return values == null ? List.of() : new ArrayList<>(values);
    }

    public PhotoResponse toPhoto(ProfilePhoto photo) {
        return new PhotoResponse(
                photo.getId(),
                photo.getPhotoUrl(),
                photo.getThumbnailUrl(),
                photo.isPrimaryPhoto(),
                photo.getDisplayOrder(),
                photo.getPrivacyLevel(),
                photo.getModerationStatus(),
                photo.getRejectionReason(),
                photo.getUploadedAt()
        );
    }

    public boolean isPubliclySearchable(MatrimonyProfile profile) {
        return profile.getProfileStatus() == ProfileStatus.ACTIVE && profile.getUser().getDeletedAt() == null;
    }
}
