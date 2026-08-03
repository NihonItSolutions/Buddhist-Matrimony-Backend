package com.matrimony.backend.service;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.AdminRequests;
import com.matrimony.backend.dto.response.CatalogResponses.MembershipPlanResponse;
import com.matrimony.backend.dto.response.CatalogResponses.MasterDataResponse;
import com.matrimony.backend.dto.response.PaymentResponses.SubscriptionResponse;
import com.matrimony.backend.enums.MasterDataType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CatalogService {
    List<MembershipPlanResponse> activePlans();

    MembershipPlanResponse plan(String code);

    SubscriptionResponse mySubscription();

    Object mySubscriptionUsage();

    void cancelMySubscription();

    MembershipPlanResponse createPlan(AdminRequests.MembershipPlanRequest request);

    MembershipPlanResponse updatePlan(Long id, AdminRequests.MembershipPlanRequest request);

    MembershipPlanResponse updatePlanStatus(Long id, AdminRequests.StatusRequest request);

    void deletePlan(Long id);

    List<MasterDataResponse> master(MasterDataType type, Long parentId);

    PageResponse<MasterDataResponse> adminMaster(Pageable pageable);
}
