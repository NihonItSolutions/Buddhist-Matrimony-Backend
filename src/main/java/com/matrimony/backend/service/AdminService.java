package com.matrimony.backend.service;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.AdminRequests;
import com.matrimony.backend.dto.response.AdminDashboardResponse;
import com.matrimony.backend.dto.response.PaymentResponses.PaymentResponse;
import com.matrimony.backend.dto.response.PaymentResponses.SubscriptionResponse;
import com.matrimony.backend.dto.response.PhotoResponse;
import com.matrimony.backend.dto.response.ProfileDetailsResponse;
import com.matrimony.backend.dto.response.UserMeResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminService {
    AdminDashboardResponse dashboard(LocalDate from, LocalDate to);

    PageResponse<UserMeResponse> users(Pageable pageable);

    UserMeResponse user(Long id);

    void activateUser(Long id);

    void suspendUser(Long id);

    void updateRole(Long id, AdminRequests.RoleUpdateRequest request);

    void deleteUser(Long id);

    PageResponse<ProfileDetailsResponse> profiles(Pageable pageable);

    ProfileDetailsResponse profile(Long id);

    void approveProfile(Long id);

    void rejectProfile(Long id, AdminRequests.RejectionRequest request);

    void suspendProfile(Long id);

    void reactivateProfile(Long id);

    PageResponse<PhotoResponse> pendingPhotos(Pageable pageable);

    PhotoResponse photo(Long id);

    void approvePhoto(Long id);

    void rejectPhoto(Long id, AdminRequests.RejectionRequest request);

    PageResponse<?> reports(Pageable pageable);

    void resolveReport(Long id, AdminRequests.ReportReviewRequest request);

    PageResponse<PaymentResponse> payments(Pageable pageable);

    PageResponse<SubscriptionResponse> subscriptions(Pageable pageable);
}
