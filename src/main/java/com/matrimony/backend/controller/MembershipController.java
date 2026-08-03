package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.response.CatalogResponses.MembershipPlanResponse;
import com.matrimony.backend.dto.response.PaymentResponses.SubscriptionResponse;
import com.matrimony.backend.service.CatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Membership")
public class MembershipController {
    private final CatalogService service;

    @GetMapping("/api/membership-plans")
    ApiResponse<List<MembershipPlanResponse>> plans() {
        return ApiResponse.ok("Membership plans", service.activePlans());
    }

    @GetMapping("/api/membership-plans/{planCode}")
    ApiResponse<MembershipPlanResponse> plan(@PathVariable String planCode) {
        return ApiResponse.ok("Membership plan", service.plan(planCode));
    }

    @GetMapping("/api/subscriptions/me")
    ApiResponse<SubscriptionResponse> subscription() {
        return ApiResponse.ok("Subscription", service.mySubscription());
    }

    @GetMapping("/api/subscriptions/me/usage")
    ApiResponse<Object> usage() {
        return ApiResponse.ok("Subscription usage", service.mySubscriptionUsage());
    }

    @PostMapping("/api/subscriptions/me/cancel")
    ApiResponse<Void> cancel() {
        service.cancelMySubscription();
        return ApiResponse.ok("Subscription cancelled", null);
    }
}
