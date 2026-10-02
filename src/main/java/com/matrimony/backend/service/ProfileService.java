package com.matrimony.backend.service;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.MatchRecommendationResponse;
import com.matrimony.backend.dto.response.PhotoResponse;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.dto.response.ProfileCompletionResponse;
import com.matrimony.backend.dto.response.ProfileDetailsResponse;
import com.matrimony.backend.enums.PhotoPrivacy;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    ProfileDetailsResponse create(ProfileCreateRequest request);

    ProfileDetailsResponse me();

    ProfileDetailsResponse updateBasic(BasicDetailsRequest request);

    ProfileDetailsResponse updatePersonal(PersonalDetailsRequest request);

    ProfileDetailsResponse updateReligious(ReligiousDetailsRequest request);

    ProfileDetailsResponse updateLocation(LocationDetailsRequest request);

    ProfileDetailsResponse updateEducation(EducationDetailsRequest request);

    ProfileDetailsResponse updateCareer(CareerDetailsRequest request);

    ProfileDetailsResponse updateFamily(FamilyDetailsRequest request);

    ProfileDetailsResponse updateLifestyle(LifestyleDetailsRequest request);

    ProfileDetailsResponse updateHoroscope(HoroscopeDetailsRequest request);

    ProfileDetailsResponse updateAbout(AboutRequest request);

    ProfileCompletionResponse completion();

    void submitForApproval();

    void deactivate();

    void reactivate();

    void delete();

    PhotoResponse uploadPhoto(MultipartFile file);

    String uploadDocument(MultipartFile file);

    List<PhotoResponse> photos();

    PhotoResponse markPrimary(Long photoId);

    PhotoResponse updatePhotoPrivacy(Long photoId, PhotoPrivacy privacy);

    void reorderPhotos(List<Long> photoIds);

    void deletePhoto(Long photoId);

    PartnerPreferenceRequest getPreference();

    PartnerPreferenceRequest updatePreference(PartnerPreferenceRequest request);

    PageResponse<ProfileCardResponse> search(ProfileSearchRequest request, Pageable pageable);

    ProfileDetailsResponse details(String matrimonyId);

    PageResponse<MatchRecommendationResponse> recommendations(Pageable pageable);

    PageResponse<ProfileCardResponse> simpleProfilePage(String type, Pageable pageable);
}
