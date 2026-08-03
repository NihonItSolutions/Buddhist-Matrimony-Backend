package com.matrimony.backend.mapper;

import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.PhotoResponse;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.dto.response.ProfileDetailsResponse;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.ModerationStatus;
import com.matrimony.backend.enums.ProfileStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class ProfileMapper {
    public Integer age(LocalDate dob) {
        return dob == null ? null : Period.between(dob, LocalDate.now()).getYears();
    }

    public String displayName(MatrimonyProfile profile) {
        String initial = profile.getLastName() == null || profile.getLastName().isBlank()
                ? ""
                : " " + profile.getLastName().charAt(0) + ".";
        return profile.getFirstName() + initial;
    }

    public ProfileCardResponse toCard(MatrimonyProfile profile,
                                      EducationDetails education,
                                      CareerDetails career,
                                      ProfilePhoto primaryPhoto,
                                      boolean shortlisted,
                                      String interestStatus,
                                      int matchScore) {
        boolean approvedPhoto = primaryPhoto != null && primaryPhoto.getModerationStatus() == ModerationStatus.APPROVED;
        return new ProfileCardResponse(
                profile.getUser().getMatrimonyId(),
                displayName(profile),
                age(profile.getDateOfBirth()),
                profile.getHeightInCm(),
                profile.getCity(),
                profile.getState(),
                education == null ? null : education.getHighestEducation(),
                career == null ? null : career.getOccupation(),
                approvedPhoto ? primaryPhoto.getPhotoUrl() : null,
                approvedPhoto,
                profile.getUser().isEmailVerified() || profile.getUser().isMobileVerified(),
                profile.getLastActiveAt(),
                matchScore,
                shortlisted,
                interestStatus
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
                                            boolean canSeePrivatePhoto) {
        List<PhotoResponse> visiblePhotos = photos.stream()
                .filter(photo -> photo.getModerationStatus() == ModerationStatus.APPROVED || canSeePrivatePhoto)
                .map(this::toPhoto)
                .toList();
        return new ProfileDetailsResponse(
                profile.getUser().getMatrimonyId(),
                profile.getProfileCreatedFor(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getLastName(),
                profile.getGender(),
                canSeeDob ? profile.getDateOfBirth() : null,
                age(profile.getDateOfBirth()),
                profile.getHeightInCm(),
                profile.getWeightInKg(),
                profile.getMaritalStatus(),
                profile.getNumberOfChildren(),
                profile.getChildrenLivingStatus(),
                profile.getPhysicalStatus(),
                profile.getMotherTongue(),
                profile.getReligion(),
                profile.getCommunity(),
                profile.getSubCommunity(),
                profile.getCasteNoBar(),
                profile.getGothra(),
                profile.getManglikStatus(),
                profile.getCountry(),
                profile.getState(),
                profile.getDistrict(),
                profile.getCity(),
                profile.getAboutMe(),
                profile.getProfileStatus(),
                profile.getProfileCompleteness(),
                profile.getLastActiveAt(),
                education == null ? null : new EducationDetailsRequest(education.getHighestEducation(), education.getEducationSpecialization(), education.getCollegeName(), education.getAdditionalQualification()),
                career == null ? null : new CareerDetailsRequest(career.getEmployedIn(), career.getOccupation(), career.getCompanyName(), career.getJobTitle(), career.getAnnualIncome(), career.getIncomeCurrency(), career.getWorkCountry(), career.getWorkState(), career.getWorkCity()),
                family == null ? null : new FamilyDetailsRequest(family.getFamilyType(), family.getFamilyStatus(), family.getFamilyValues(), family.getFatherStatus(), family.getFatherOccupation(), family.getMotherStatus(), family.getMotherOccupation(), family.getNumberOfBrothers(), family.getMarriedBrothers(), family.getNumberOfSisters(), family.getMarriedSisters(), family.getFamilyCountry(), family.getFamilyState(), family.getFamilyCity(), family.getFamilyDescription()),
                lifestyle == null ? null : new LifestyleDetailsRequest(lifestyle.getDiet(), lifestyle.getSmokingHabit(), lifestyle.getDrinkingHabit(), lifestyle.getHobbies(), lifestyle.getInterests(), lifestyle.getLanguagesKnown()),
                horoscope == null ? null : new HoroscopeDetailsRequest(horoscope.getDateOfBirth(), horoscope.getTimeOfBirth(), horoscope.getPlaceOfBirth(), horoscope.getRashi(), horoscope.getNakshatra(), horoscope.getManglikStatus(), horoscope.getHoroscopeAvailable(), horoscope.getHoroscopeDocumentUrl()),
                preference == null ? null : new PartnerPreferenceRequest(preference.getMinimumAge(), preference.getMaximumAge(), preference.getMinimumHeight(), preference.getMaximumHeight(), preference.getMaritalStatuses(), preference.getReligions(), preference.getCommunities(), preference.getSubCommunities(), preference.getMotherTongues(), preference.getCountries(), preference.getStates(), preference.getCities(), preference.getEducationLevels(), preference.getOccupations(), preference.getMinimumIncome(), preference.getMaximumIncome(), preference.getDietPreferences(), preference.getPhysicalStatusPreferences(), preference.getManglikPreferences(), preference.getDescription()),
                visiblePhotos
        );
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
