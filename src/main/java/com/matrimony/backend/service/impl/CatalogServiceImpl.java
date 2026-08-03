package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.AdminRequests;
import com.matrimony.backend.dto.response.CatalogResponses.MembershipPlanResponse;
import com.matrimony.backend.dto.response.CatalogResponses.MasterDataResponse;
import com.matrimony.backend.dto.response.PaymentResponses.SubscriptionResponse;
import com.matrimony.backend.entity.MasterData;
import com.matrimony.backend.entity.MembershipPlan;
import com.matrimony.backend.entity.UserSubscription;
import com.matrimony.backend.enums.MasterDataType;
import com.matrimony.backend.enums.SubscriptionStatus;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.repository.MasterDataRepository;
import com.matrimony.backend.repository.MembershipPlanRepository;
import com.matrimony.backend.repository.UserSubscriptionRepository;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final MembershipPlanRepository planRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final MasterDataRepository masterDataRepository;
    private final CurrentUser currentUser;

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlanResponse> activePlans() {
        return planRepository.findByActiveTrueOrderByPriceAsc().stream().map(this::toPlan).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipPlanResponse plan(String code) {
        return toPlan(planRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Membership plan not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse mySubscription() {
        return subscriptionRepository.findFirstByUserIdAndStatusOrderByEndDateDesc(currentUser.get().getId(), SubscriptionStatus.ACTIVE)
                .map(this::toSubscription)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Object mySubscriptionUsage() {
        SubscriptionResponse subscription = mySubscription();
        return Map.of("subscription", subscription == null ? "NONE" : subscription, "premiumFilters", subscription != null);
    }

    @Override
    @Transactional
    public void cancelMySubscription() {
        subscriptionRepository.findFirstByUserIdAndStatusOrderByEndDateDesc(currentUser.get().getId(), SubscriptionStatus.ACTIVE)
                .ifPresent(subscription -> subscription.setStatus(SubscriptionStatus.CANCELLED));
    }

    @Override
    @Transactional
    public MembershipPlanResponse createPlan(AdminRequests.MembershipPlanRequest request) {
        MembershipPlan plan = new MembershipPlan();
        apply(plan, request);
        return toPlan(planRepository.save(plan));
    }

    @Override
    @Transactional
    public MembershipPlanResponse updatePlan(Long id, AdminRequests.MembershipPlanRequest request) {
        MembershipPlan plan = planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Membership plan not found"));
        apply(plan, request);
        return toPlan(plan);
    }

    @Override
    @Transactional
    public MembershipPlanResponse updatePlanStatus(Long id, AdminRequests.StatusRequest request) {
        MembershipPlan plan = planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Membership plan not found"));
        plan.setActive(request.active());
        return toPlan(plan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        MembershipPlan plan = planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Membership plan not found"));
        plan.setActive(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterDataResponse> master(MasterDataType type, Long parentId) {
        List<MasterData> rows = parentId == null
                ? masterDataRepository.findByTypeAndActiveTrueOrderByDisplayOrderAscNameAsc(type)
                : masterDataRepository.findByTypeAndParentIdAndActiveTrueOrderByDisplayOrderAscNameAsc(type, parentId);
        return rows.stream().map(this::toMaster).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MasterDataResponse> adminMaster(Pageable pageable) {
        return PageResponse.from(masterDataRepository.findAll(pageable).map(this::toMaster));
    }

    private void apply(MembershipPlan plan, AdminRequests.MembershipPlanRequest request) {
        plan.setName(request.name());
        plan.setCode(request.code());
        plan.setDescription(request.description());
        plan.setPrice(request.price());
        plan.setCurrency(request.currency());
        plan.setDurationDays(request.durationDays());
        plan.setDailyInterestLimit(request.dailyInterestLimit());
        plan.setContactViewLimit(request.contactViewLimit());
        plan.setMessageLimit(request.messageLimit());
        plan.setProfileBoostAllowed(request.profileBoostAllowed());
        plan.setAssistedService(request.assistedService());
        plan.setActive(request.active());
    }

    private MembershipPlanResponse toPlan(MembershipPlan plan) {
        return new MembershipPlanResponse(plan.getId(), plan.getName(), plan.getCode(), plan.getDescription(), plan.getPrice(), plan.getCurrency(), plan.getDurationDays(), plan.getDailyInterestLimit(), plan.getContactViewLimit(), plan.getMessageLimit(), plan.isProfileBoostAllowed(), plan.isAssistedService(), plan.isActive());
    }

    private SubscriptionResponse toSubscription(UserSubscription subscription) {
        return new SubscriptionResponse(subscription.getId(), subscription.getMembershipPlan().getCode(), subscription.getStatus(), subscription.getStartDate(), subscription.getEndDate(), subscription.getRemainingContactViews(), subscription.getRemainingMessages(), subscription.getRemainingInterestsToday());
    }

    private MasterDataResponse toMaster(MasterData data) {
        return new MasterDataResponse(data.getId(), data.getType().name(), data.getName(), data.getCode(), data.getParent() == null ? null : data.getParent().getId(), data.isActive(), data.getDisplayOrder());
    }
}
