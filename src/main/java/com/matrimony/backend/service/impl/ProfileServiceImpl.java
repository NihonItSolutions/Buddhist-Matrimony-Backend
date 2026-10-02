package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.*;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.*;
import com.matrimony.backend.exception.*;
import com.matrimony.backend.mapper.ProfileMapper;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.FileStorageService;
import com.matrimony.backend.service.MatchRecommendationService;
import com.matrimony.backend.service.ProfileCompletionService;
import com.matrimony.backend.service.ProfileService;
import com.matrimony.backend.service.EntitlementService;
import com.matrimony.backend.specification.ProfileSpecifications;
import com.matrimony.backend.util.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final CurrentUser currentUser;
    private final AppProperties properties;
    private final MatrimonyProfileRepository profileRepository;
    private final EducationDetailsRepository educationRepository;
    private final CareerDetailsRepository careerRepository;
    private final FamilyDetailsRepository familyRepository;
    private final LifestyleDetailsRepository lifestyleRepository;
    private final HoroscopeDetailsRepository horoscopeRepository;
    private final PartnerPreferenceRepository preferenceRepository;
    private final ProfilePhotoRepository photoRepository;
    private final ProfileViewRepository profileViewRepository;
    private final ShortlistedProfileRepository shortlistRepository;
    private final InterestRepository interestRepository;
    private final ContactRequestRepository contactRequestRepository;
    private final BlockedProfileRepository blockedProfileRepository;
    private final FileStorageService fileStorageService;
    private final ProfileCompletionService completionService;
    private final MatchRecommendationService recommendationService;
    private final EntitlementService entitlementService;
    private final ProfileMapper mapper;

    @Override
    @Transactional
    public ProfileDetailsResponse create(ProfileCreateRequest request) {
        User user = currentUser.get();
        profileRepository.findByUser(user).ifPresent(profile -> {
            throw new DuplicateResourceException("Profile already exists");
        });
        MatrimonyProfile profile = new MatrimonyProfile();
        profile.setUser(user);
        profile.setProfileCreatedFor(request.profileCreatedFor());
        profile.setGender(request.gender());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setLastActiveAt(LocalDateTime.now());
        profileRepository.save(profile);
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDetailsResponse me() {
        return detailsFor(currentProfile(), true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateBasic(BasicDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        profile.setProfileCreatedFor(request.profileCreatedFor());
        profile.setFirstName(request.firstName());
        profile.setMiddleName(request.middleName());
        profile.setLastName(request.lastName());
        profile.setGender(request.gender());
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updatePersonal(PersonalDetailsRequest request) {
        validateAge(request.dateOfBirth());
        MatrimonyProfile profile = currentProfile();
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setHeightInCm(request.heightInCm());
        profile.setWeightInKg(request.weightInKg());
        profile.setBloodGroup(request.bloodGroup());
        profile.setMaritalStatus(request.maritalStatus());
        profile.setNumberOfChildren(request.numberOfChildren());
        profile.setChildrenLivingStatus(request.childrenLivingStatus());
        profile.setPhysicalStatus(request.physicalStatus());
        if (request.leavingCertificateUrl() != null) profile.setLeavingCertificateUrl(request.leavingCertificateUrl());
        if (request.aadharCardUrl() != null) profile.setAadharCardUrl(request.aadharCardUrl());
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateReligious(ReligiousDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        profile.setMotherTongue(request.motherTongue());
        profile.setReligion(request.religion());
        profile.setCommunity(request.community());
        profile.setSubCommunity(request.subCommunity());
        profile.setCasteNoBar(request.casteNoBar());
        profile.setGothra(request.gothra());
        profile.setManglikStatus(request.manglikStatus());
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateLocation(LocationDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        profile.setCitizenship(request.citizenship());
        profile.setCountry(request.country());
        profile.setState(request.state());
        profile.setDistrict(request.district());
        profile.setCity(request.city());
        profile.setPostalCode(request.postalCode());
        profile.setResidencyStatus(request.residencyStatus());
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateEducation(EducationDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        EducationDetails details = educationRepository.findByProfileId(profile.getId()).orElseGet(() -> {
            EducationDetails education = new EducationDetails();
            education.setProfile(profile);
            return education;
        });
        details.setHighestEducation(request.highestEducation());
        details.setEducationSpecialization(request.educationSpecialization());
        details.setCollegeName(request.collegeName());
        details.setAdditionalQualification(request.additionalQualification());
        educationRepository.save(details);
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateCareer(CareerDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        CareerDetails details = careerRepository.findByProfileId(profile.getId()).orElseGet(() -> {
            CareerDetails career = new CareerDetails();
            career.setProfile(profile);
            return career;
        });
        details.setEmployedIn(request.employedIn());
        details.setOccupation(request.occupation());
        details.setCompanyName(request.companyName());
        details.setJobTitle(request.jobTitle());
        details.setAnnualIncome(request.annualIncome());
        details.setIncomeCurrency(request.incomeCurrency());
        details.setWorkCountry(request.workCountry());
        details.setWorkState(request.workState());
        details.setWorkCity(request.workCity());
        careerRepository.save(details);
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateFamily(FamilyDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        FamilyDetails details = familyRepository.findByProfileId(profile.getId()).orElseGet(() -> {
            FamilyDetails family = new FamilyDetails();
            family.setProfile(profile);
            return family;
        });
        details.setFamilyType(request.familyType());
        details.setFamilyStatus(request.familyStatus());
        details.setFamilyValues(request.familyValues());
        details.setFatherStatus(request.fatherStatus());
        details.setFatherOccupation(request.fatherOccupation());
        details.setMotherStatus(request.motherStatus());
        details.setMotherOccupation(request.motherOccupation());
        details.setNumberOfBrothers(request.numberOfBrothers());
        details.setMarriedBrothers(request.marriedBrothers());
        details.setNumberOfSisters(request.numberOfSisters());
        details.setMarriedSisters(request.marriedSisters());
        details.setFamilyCountry(request.familyCountry());
        details.setFamilyState(request.familyState());
        details.setFamilyCity(request.familyCity());
        details.setFamilyDescription(TextSanitizer.clean(request.familyDescription()));
        familyRepository.save(details);
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateLifestyle(LifestyleDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        LifestyleDetails details = lifestyleRepository.findByProfileId(profile.getId()).orElseGet(() -> {
            LifestyleDetails lifestyle = new LifestyleDetails();
            lifestyle.setProfile(profile);
            return lifestyle;
        });
        details.setDiet(request.diet());
        details.setSmokingHabit(request.smokingHabit());
        details.setDrinkingHabit(request.drinkingHabit());
        details.setHobbies(request.hobbies());
        details.setInterests(request.interests());
        details.setLanguagesKnown(request.languagesKnown());
        lifestyleRepository.save(details);
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateHoroscope(HoroscopeDetailsRequest request) {
        MatrimonyProfile profile = currentProfile();
        HoroscopeDetails details = horoscopeRepository.findByProfileId(profile.getId()).orElseGet(() -> {
            HoroscopeDetails horoscope = new HoroscopeDetails();
            horoscope.setProfile(profile);
            return horoscope;
        });
        details.setDateOfBirth(request.dateOfBirth());
        details.setTimeOfBirth(request.timeOfBirth());
        details.setPlaceOfBirth(request.placeOfBirth());
        details.setRashi(request.rashi());
        details.setNakshatra(request.nakshatra());
        details.setManglikStatus(request.manglikStatus());
        details.setHoroscopeAvailable(request.horoscopeAvailable());
        details.setHoroscopeDocumentUrl(request.horoscopeDocumentUrl());
        horoscopeRepository.save(details);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional
    public ProfileDetailsResponse updateAbout(AboutRequest request) {
        MatrimonyProfile profile = currentProfile();
        profile.setAboutMe(TextSanitizer.clean(request.aboutMe()));
        recalculate(profile);
        return detailsFor(profile, true);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileCompletionResponse completion() {
        return completionService.calculate(currentProfile());
    }

    @Override
    @Transactional
    public void submitForApproval() {
        MatrimonyProfile profile = currentProfile();
        ProfileCompletionResponse completion = completionService.calculate(profile);
        if (!completion.canSubmitForApproval()) {
            throw new ProfileIncompleteException("Profile is incomplete");
        }
        profile.setProfileStatus(ProfileStatus.PENDING_APPROVAL);
    }

    @Override
    @Transactional
    public void deactivate() {
        currentProfile().setProfileStatus(ProfileStatus.DEACTIVATED);
    }

    @Override
    @Transactional
    public void reactivate() {
        currentProfile().setProfileStatus(ProfileStatus.DRAFT);
    }

    @Override
    @Transactional
    public void delete() {
        User user = currentUser.get();
        user.setAccountStatus(AccountStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        currentProfile().setProfileStatus(ProfileStatus.DEACTIVATED);
    }

    @Override
    @Transactional
    public PhotoResponse uploadPhoto(MultipartFile file) {
        MatrimonyProfile profile = currentProfile();
        if (photoRepository.countByProfileId(profile.getId()) >= properties.profile().maxPhotos()) {
            throw new InvalidRequestException("Maximum profile photo limit reached");
        }
        FileStorageService.StoredFile stored = fileStorageService.storeProfilePhoto(file, profile.getUser().getMatrimonyId());
        ProfilePhoto photo = new ProfilePhoto();
        photo.setProfile(profile);
        photo.setStorageKey(stored.storageKey());
        photo.setPhotoUrl(stored.url());
        photo.setThumbnailUrl(stored.thumbnailUrl());
        photo.setDisplayOrder((int) photoRepository.countByProfileId(profile.getId()) + 1);
        photo.setPrimaryPhoto(photoRepository.countByProfileId(profile.getId()) == 0);
        photo.setUploadedAt(LocalDateTime.now());
        photoRepository.save(photo);
        recalculate(profile);
        return mapper.toPhoto(photo);
    }

    @Override
    @Transactional
    public String uploadDocument(MultipartFile file) {
        MatrimonyProfile profile = currentProfile();
        return fileStorageService.storeDocument(file, profile.getUser().getMatrimonyId()).url();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhotoResponse> photos() {
        return photoRepository.findByProfileIdOrderByDisplayOrderAsc(currentProfile().getId()).stream()
                .map(mapper::toPhoto)
                .toList();
    }

    @Override
    @Transactional
    public PhotoResponse markPrimary(Long photoId) {
        MatrimonyProfile profile = currentProfile();
        List<ProfilePhoto> photos = photoRepository.findByProfileIdOrderByDisplayOrderAsc(profile.getId());
        ProfilePhoto selected = photos.stream()
                .filter(photo -> photo.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
        photos.forEach(photo -> photo.setPrimaryPhoto(photo.getId().equals(photoId)));
        return mapper.toPhoto(selected);
    }

    @Override
    @Transactional
    public PhotoResponse updatePhotoPrivacy(Long photoId, PhotoPrivacy privacy) {
        ProfilePhoto photo = ownedPhoto(photoId);
        photo.setPrivacyLevel(privacy);
        return mapper.toPhoto(photo);
    }

    @Override
    @Transactional
    public void reorderPhotos(List<Long> photoIds) {
        MatrimonyProfile profile = currentProfile();
        List<ProfilePhoto> photos = photoRepository.findByProfileIdOrderByDisplayOrderAsc(profile.getId());
        for (int i = 0; i < photoIds.size(); i++) {
            Long id = photoIds.get(i);
            int displayOrder = i + 1;
            photos.stream()
                    .filter(photo -> photo.getId().equals(id))
                    .findFirst()
                    .ifPresent(photo -> photo.setDisplayOrder(displayOrder));
        }
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId) {
        ProfilePhoto photo = ownedPhoto(photoId);
        photoRepository.delete(photo);
        fileStorageService.delete(photo.getStorageKey());
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerPreferenceRequest getPreference() {
        PartnerPreference preference = preferenceRepository.findByProfileId(currentProfile().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner preference not found"));
        return toPreferenceRequest(preference);
    }

    @Override
    @Transactional
    public PartnerPreferenceRequest updatePreference(PartnerPreferenceRequest request) {
        validatePreference(request);
        MatrimonyProfile profile = currentProfile();
        PartnerPreference preference = preferenceRepository.findByProfileId(profile.getId()).orElseGet(() -> {
            PartnerPreference created = new PartnerPreference();
            created.setProfile(profile);
            return created;
        });
        preference.setMinimumAge(request.minimumAge());
        preference.setMaximumAge(request.maximumAge());
        preference.setMinimumHeight(request.minimumHeight());
        preference.setMaximumHeight(request.maximumHeight());
        preference.setMinimumIncome(request.minimumIncome());
        preference.setMaximumIncome(request.maximumIncome());
        replace(preference.getMaritalStatuses(), request.maritalStatuses());
        replace(preference.getReligions(), request.religions());
        replace(preference.getCommunities(), request.communities());
        replace(preference.getSubCommunities(), request.subCommunities());
        replace(preference.getMotherTongues(), request.motherTongues());
        replace(preference.getCountries(), request.countries());
        replace(preference.getStates(), request.states());
        replace(preference.getCities(), request.cities());
        replace(preference.getEducationLevels(), request.educationLevels());
        replace(preference.getOccupations(), request.occupations());
        replace(preference.getDietPreferences(), request.dietPreferences());
        replace(preference.getPhysicalStatusPreferences(), request.physicalStatusPreferences());
        replace(preference.getManglikPreferences(), request.manglikPreferences());
        preference.setDescription(TextSanitizer.clean(request.description()));
        preferenceRepository.save(preference);
        recalculate(profile);
        return toPreferenceRequest(preference);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProfileCardResponse> search(ProfileSearchRequest request, Pageable pageable) {
        MatrimonyProfile current = currentProfile();
        Page<MatrimonyProfile> page = profileRepository.findAll(ProfileSpecifications.search(request, current.getId(), oppositeGender(current.getGender())), pageable);
        List<ProfileCardResponse> content = page.getContent().stream()
                .filter(profile -> !blockedProfileRepository.existsByBlockedByProfileIdAndBlockedProfileIdOrBlockedByProfileIdAndBlockedProfileId(current.getId(), profile.getId(), profile.getId(), current.getId()))
                .map(profile -> toCard(current, profile))
                .toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    @Override
    @Transactional
    public ProfileDetailsResponse details(String matrimonyId) {
        MatrimonyProfile current = currentProfile();
        MatrimonyProfile target = profileRepository.findByUserMatrimonyId(matrimonyId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (!target.getId().equals(current.getId())) {
            if (target.getProfileStatus() != ProfileStatus.ACTIVE) {
                throw new ResourceNotFoundException("Profile not found");
            }
            if (blockedProfileRepository.existsByBlockedByProfileIdAndBlockedProfileIdOrBlockedByProfileIdAndBlockedProfileId(current.getId(), target.getId(), target.getId(), current.getId())) {
                throw new ForbiddenOperationException("Profile is unavailable");
            }
            recordProfileView(current, target);
        }
        return detailsFor(target, target.getId().equals(current.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MatchRecommendationResponse> recommendations(Pageable pageable) {
        MatrimonyProfile current = currentProfile();
        Page<MatrimonyProfile> page = matchPage(current, pageable);
        List<MatchRecommendationResponse> content = page.getContent().stream()
                .map(profile -> recommendationService.score(current, profile))
                .toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProfileCardResponse> simpleProfilePage(String type, Pageable pageable) {
        MatrimonyProfile current = currentProfile();
        Page<MatrimonyProfile> profiles = matchPage(current, pageable);
        List<ProfileCardResponse> content = profiles.getContent().stream()
                .map(profile -> toCard(current, profile))
                .toList();
        return new PageResponse<>(content, profiles.getNumber(), profiles.getSize(), profiles.getTotalElements(), profiles.getTotalPages(), profiles.isFirst(), profiles.isLast());
    }

    private Page<MatrimonyProfile> matchPage(MatrimonyProfile current, Pageable pageable) {
        Gender gender = oppositeGender(current.getGender());
        if (gender == null) {
            return profileRepository.findByProfileStatusAndIdNot(ProfileStatus.ACTIVE, current.getId(), pageable);
        }
        return profileRepository.findByProfileStatusAndGenderAndIdNot(ProfileStatus.ACTIVE, gender, current.getId(), pageable);
    }

    private Gender oppositeGender(Gender gender) {
        if (gender == Gender.MALE) {
            return Gender.FEMALE;
        }
        if (gender == Gender.FEMALE) {
            return Gender.MALE;
        }
        return null;
    }

    private MatrimonyProfile currentProfile() {
        return profileRepository.findByUser(currentUser.get())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private ProfilePhoto ownedPhoto(Long photoId) {
        MatrimonyProfile profile = currentProfile();
        ProfilePhoto photo = photoRepository.findById(photoId).orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
        if (!photo.getProfile().getId().equals(profile.getId())) {
            throw new ForbiddenOperationException("Photo does not belong to current profile");
        }
        return photo;
    }

    private void validateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now().minusYears(properties.profile().minimumAge()))) {
            throw new InvalidRequestException("Profile does not meet the minimum age requirement");
        }
    }

    private void validatePreference(PartnerPreferenceRequest request) {
        if (request.minimumAge() != null && request.maximumAge() != null && request.minimumAge() > request.maximumAge()) {
            throw new InvalidRequestException("Minimum age cannot be greater than maximum age");
        }
        if (request.minimumHeight() != null && request.maximumHeight() != null && request.minimumHeight() > request.maximumHeight()) {
            throw new InvalidRequestException("Minimum height cannot be greater than maximum height");
        }
        if (request.minimumIncome() != null && request.maximumIncome() != null && request.minimumIncome().compareTo(request.maximumIncome()) > 0) {
            throw new InvalidRequestException("Minimum income cannot be greater than maximum income");
        }
    }

    private void recordProfileView(MatrimonyProfile viewer, MatrimonyProfile viewed) {
        LocalDateTime after = LocalDateTime.now().minus(properties.profile().profileViewDeduplicationPeriod());
        if (profileViewRepository.findFirstByViewerProfileIdAndViewedProfileIdAndViewedAtAfter(viewer.getId(), viewed.getId(), after).isEmpty()) {
            ProfileView view = new ProfileView();
            view.setViewerProfile(viewer);
            view.setViewedProfile(viewed);
            view.setViewedAt(LocalDateTime.now());
            profileViewRepository.save(view);
        }
    }

    private void recalculate(MatrimonyProfile profile) {
        ProfileCompletionResponse completion = completionService.calculate(profile);
        profile.setProfileCompleteness(completion.percentage());
    }

    private ProfileDetailsResponse detailsFor(MatrimonyProfile profile, boolean owner) {
        MatrimonyProfile current = owner ? profile : currentProfile();
        boolean sameProfile = current.getId().equals(profile.getId());
        boolean hasActivePlan = owner || entitlementService.hasActivePlan(current.getUser());
        boolean shortlisted = !sameProfile && shortlistRepository.existsByOwnerProfileIdAndShortlistedProfileId(current.getId(), profile.getId());
        String interestStatus = !sameProfile ? interestRepository.findActiveBetween(current.getId(), profile.getId(), List.of(InterestStatus.PENDING, InterestStatus.ACCEPTED))
                .map(interest -> interest.getStatus().name())
                .orElse(null) : null;
        String contactRequestStatus = !sameProfile ? contactRequestRepository.findFirstByRequesterProfileIdAndReceiverProfileIdOrderByRequestedAtDesc(current.getId(), profile.getId())
                .map(request -> request.getStatus().name())
                .orElse(null) : null;
        return mapper.toDetails(
                profile,
                educationRepository.findByProfileId(profile.getId()).orElse(null),
                careerRepository.findByProfileId(profile.getId()).orElse(null),
                familyRepository.findByProfileId(profile.getId()).orElse(null),
                lifestyleRepository.findByProfileId(profile.getId()).orElse(null),
                horoscopeRepository.findByProfileId(profile.getId()).orElse(null),
                preferenceRepository.findByProfileId(profile.getId()).orElse(null),
                photoRepository.findByProfileIdOrderByDisplayOrderAsc(profile.getId()),
                owner,
                owner,
                shortlisted,
                interestStatus,
                contactRequestStatus,
                hasActivePlan
        );
    }

    private ProfileCardResponse toCard(MatrimonyProfile current, MatrimonyProfile target) {
        EducationDetails education = educationRepository.findByProfileId(target.getId()).orElse(null);
        CareerDetails career = careerRepository.findByProfileId(target.getId()).orElse(null);
        ProfilePhoto photo = photoRepository.findDisplayPhoto(target.getId()).orElse(null);
        boolean shortlisted = shortlistRepository.existsByOwnerProfileIdAndShortlistedProfileId(current.getId(), target.getId());
        String interestStatus = interestRepository.findActiveBetween(current.getId(), target.getId(), List.of(InterestStatus.PENDING, InterestStatus.ACCEPTED))
                .map(interest -> interest.getStatus().name())
                .orElse(null);
        int score = recommendationService.score(current, target).matchScore();
        boolean hasActivePlan = entitlementService.hasActivePlan(current.getUser());
        return mapper.toCard(target, education, career, photo, shortlisted, interestStatus, score, hasActivePlan);
    }

    private PartnerPreferenceRequest toPreferenceRequest(PartnerPreference preference) {
        return new PartnerPreferenceRequest(
                preference.getMinimumAge(),
                preference.getMaximumAge(),
                preference.getMinimumHeight(),
                preference.getMaximumHeight(),
                new ArrayList<>(preference.getMaritalStatuses()),
                new ArrayList<>(preference.getReligions()),
                new ArrayList<>(preference.getCommunities()),
                new ArrayList<>(preference.getSubCommunities()),
                new ArrayList<>(preference.getMotherTongues()),
                new ArrayList<>(preference.getCountries()),
                new ArrayList<>(preference.getStates()),
                new ArrayList<>(preference.getCities()),
                new ArrayList<>(preference.getEducationLevels()),
                new ArrayList<>(preference.getOccupations()),
                preference.getMinimumIncome(),
                preference.getMaximumIncome(),
                new ArrayList<>(preference.getDietPreferences()),
                new ArrayList<>(preference.getPhysicalStatusPreferences()),
                new ArrayList<>(preference.getManglikPreferences()),
                preference.getDescription()
        );
    }

    private void replace(List<String> target, List<String> source) {
        target.clear();
        if (source != null) {
            source.stream().filter(StringUtils::hasText).map(String::trim).distinct().forEach(target::add);
        }
    }
}
