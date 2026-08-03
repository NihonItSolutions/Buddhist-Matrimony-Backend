package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.AdminRequests;
import com.matrimony.backend.dto.response.*;
import com.matrimony.backend.dto.response.PaymentResponses.PaymentResponse;
import com.matrimony.backend.dto.response.PaymentResponses.SubscriptionResponse;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.*;
import com.matrimony.backend.exception.InvalidRequestException;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.mapper.ProfileMapper;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final MatrimonyProfileRepository profileRepository;
    private final EducationDetailsRepository educationRepository;
    private final CareerDetailsRepository careerRepository;
    private final FamilyDetailsRepository familyRepository;
    private final LifestyleDetailsRepository lifestyleRepository;
    private final HoroscopeDetailsRepository horoscopeRepository;
    private final PartnerPreferenceRepository preferenceRepository;
    private final ProfilePhotoRepository photoRepository;
    private final ProfileReportRepository reportRepository;
    private final InterestRepository interestRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final SupportTicketRepository ticketRepository;
    private final AuditLogRepository auditLogRepository;
    private final CurrentUser currentUser;
    private final ProfileMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse dashboard(LocalDate from, LocalDate to) {
        LocalDate start = from == null ? LocalDate.now() : from;
        LocalDate end = to == null ? LocalDate.now() : to;
        LocalDateTime fromTs = start.atStartOfDay();
        LocalDateTime toTs = end.atTime(LocalTime.MAX);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return new AdminDashboardResponse(
                userRepository.count(),
                profileRepository.countByProfileStatus(ProfileStatus.ACTIVE),
                profileRepository.countByProfileStatus(ProfileStatus.PENDING_APPROVAL),
                photoRepository.findByModerationStatus(ModerationStatus.PENDING, Pageable.unpaged()).getTotalElements(),
                profileRepository.countByGender(Gender.MALE),
                profileRepository.countByGender(Gender.FEMALE),
                interestRepository.countBySentAtBetween(todayStart, todayEnd),
                interestRepository.findAll().stream().filter(i -> i.getStatus() == InterestStatus.ACCEPTED).count(),
                subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE),
                reportRepository.findByStatus(ReportStatus.OPEN, Pageable.unpaged()).getTotalElements(),
                ticketRepository.countByStatus(SupportTicketStatus.OPEN),
                paymentRepository.sumAmountByStatusAndPaidAtBetween(PaymentStatus.SUCCESS, todayStart, todayEnd),
                paymentRepository.sumAmountByStatusAndPaidAtBetween(PaymentStatus.SUCCESS, monthStart, todayEnd),
                userRepository.findByDeletedAtIsNull(Pageable.ofSize(5)).getContent().stream().map(this::toUser).toList(),
                paymentRepository.findAll(Pageable.ofSize(5)).getContent().stream().map(this::toPayment).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserMeResponse> users(Pageable pageable) {
        return PageResponse.from(userRepository.findByDeletedAtIsNull(pageable).map(this::toUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserMeResponse user(Long id) {
        return toUser(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        User user = findUser(id);
        user.setAccountStatus(AccountStatus.ACTIVE);
        audit("USER_ACTIVATED", "User", id, null, "ACTIVE");
    }

    @Override
    @Transactional
    public void suspendUser(Long id) {
        User user = findUser(id);
        user.setAccountStatus(AccountStatus.SUSPENDED);
        audit("USER_SUSPENDED", "User", id, null, "SUSPENDED");
    }

    @Override
    @Transactional
    public void updateRole(Long id, AdminRequests.RoleUpdateRequest request) {
        User user = findUser(id);
        Role old = user.getRole();
        user.setRole(request.role());
        audit("USER_ROLE_UPDATED", "User", id, old.name(), request.role().name());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);
        user.setAccountStatus(AccountStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        audit("USER_DELETED", "User", id, null, "DELETED");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProfileDetailsResponse> profiles(Pageable pageable) {
        return PageResponse.from(profileRepository.findAll(pageable).map(this::toProfile));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDetailsResponse profile(Long id) {
        return toProfile(findProfile(id));
    }

    @Override
    @Transactional
    public void approveProfile(Long id) {
        MatrimonyProfile profile = findProfile(id);
        profile.setProfileStatus(ProfileStatus.ACTIVE);
        audit("PROFILE_APPROVED", "MatrimonyProfile", id, null, "ACTIVE");
    }

    @Override
    @Transactional
    public void rejectProfile(Long id, AdminRequests.RejectionRequest request) {
        if (request == null || request.reason() == null || request.reason().isBlank()) {
            throw new InvalidRequestException("Rejection reason is required");
        }
        MatrimonyProfile profile = findProfile(id);
        profile.setProfileStatus(ProfileStatus.REJECTED);
        audit("PROFILE_REJECTED", "MatrimonyProfile", id, null, request.reason());
    }

    @Override
    @Transactional
    public void suspendProfile(Long id) {
        findProfile(id).setProfileStatus(ProfileStatus.SUSPENDED);
        audit("PROFILE_SUSPENDED", "MatrimonyProfile", id, null, "SUSPENDED");
    }

    @Override
    @Transactional
    public void reactivateProfile(Long id) {
        findProfile(id).setProfileStatus(ProfileStatus.ACTIVE);
        audit("PROFILE_REACTIVATED", "MatrimonyProfile", id, null, "ACTIVE");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PhotoResponse> pendingPhotos(Pageable pageable) {
        return PageResponse.from(photoRepository.findByModerationStatus(ModerationStatus.PENDING, pageable).map(mapper::toPhoto));
    }

    @Override
    @Transactional(readOnly = true)
    public PhotoResponse photo(Long id) {
        return mapper.toPhoto(photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo not found")));
    }

    @Override
    @Transactional
    public void approvePhoto(Long id) {
        ProfilePhoto photo = photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
        photo.setModerationStatus(ModerationStatus.APPROVED);
        photo.setApprovedAt(LocalDateTime.now());
        photo.setApprovedBy(currentUser.get());
        audit("PHOTO_APPROVED", "ProfilePhoto", id, null, "APPROVED");
    }

    @Override
    @Transactional
    public void rejectPhoto(Long id, AdminRequests.RejectionRequest request) {
        if (request == null || request.reason() == null || request.reason().isBlank()) {
            throw new InvalidRequestException("Rejection reason is required");
        }
        ProfilePhoto photo = photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo not found"));
        photo.setModerationStatus(ModerationStatus.REJECTED);
        photo.setRejectionReason(request.reason());
        audit("PHOTO_REJECTED", "ProfilePhoto", id, null, request.reason());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> reports(Pageable pageable) {
        return PageResponse.from(reportRepository.findAll(pageable).map(report -> Map.of(
                "id", report.getId(),
                "reporterMatrimonyId", report.getReporterProfile().getUser().getMatrimonyId(),
                "reportedMatrimonyId", report.getReportedProfile().getUser().getMatrimonyId(),
                "reason", report.getReason(),
                "status", report.getStatus(),
                "createdAt", report.getCreatedAt()
        )));
    }

    @Override
    @Transactional
    public void resolveReport(Long id, AdminRequests.ReportReviewRequest request) {
        ProfileReport report = reportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        report.setReviewedBy(currentUser.get());
        report.setReviewedAt(LocalDateTime.now());
        report.setAdminComment(request.adminComment());
        if (request.suspendProfile()) {
            report.getReportedProfile().setProfileStatus(ProfileStatus.SUSPENDED);
        }
        audit("REPORT_RESOLVED", "ProfileReport", id, null, request.adminComment());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> payments(Pageable pageable) {
        return PageResponse.from(paymentRepository.findAll(pageable).map(this::toPayment));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubscriptionResponse> subscriptions(Pageable pageable) {
        return PageResponse.from(subscriptionRepository.findAll(pageable).map(this::toSubscription));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private MatrimonyProfile findProfile(Long id) {
        return profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private UserMeResponse toUser(User user) {
        int completion = profileRepository.findByUser(user).map(MatrimonyProfile::getProfileCompleteness).orElse(0);
        return new UserMeResponse(user.getId(), user.getMatrimonyId(), user.getEmail(), user.getCountryCode() + user.getMobileNumber(), user.getRole(), user.getAccountStatus(), user.isEmailVerified(), user.isMobileVerified(), completion);
    }

    private ProfileDetailsResponse toProfile(MatrimonyProfile profile) {
        return mapper.toDetails(profile,
                educationRepository.findByProfileId(profile.getId()).orElse(null),
                careerRepository.findByProfileId(profile.getId()).orElse(null),
                familyRepository.findByProfileId(profile.getId()).orElse(null),
                lifestyleRepository.findByProfileId(profile.getId()).orElse(null),
                horoscopeRepository.findByProfileId(profile.getId()).orElse(null),
                preferenceRepository.findByProfileId(profile.getId()).orElse(null),
                photoRepository.findByProfileIdOrderByDisplayOrderAsc(profile.getId()),
                true,
                true);
    }

    private PaymentResponse toPayment(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getGatewayOrderId(), payment.getGatewayPaymentId(), payment.getAmount(), payment.getCurrency(), payment.getStatus(), payment.getCreatedAt(), payment.getPaidAt());
    }

    private SubscriptionResponse toSubscription(UserSubscription subscription) {
        return new SubscriptionResponse(subscription.getId(), subscription.getMembershipPlan().getCode(), subscription.getStatus(), subscription.getStartDate(), subscription.getEndDate(), subscription.getRemainingContactViews(), subscription.getRemainingMessages(), subscription.getRemainingInterestsToday());
    }

    private void audit(String action, String entityType, Long entityId, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setActorUser(currentUser.get());
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        auditLogRepository.save(log);
    }
}
