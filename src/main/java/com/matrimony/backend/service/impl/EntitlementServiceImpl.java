package com.matrimony.backend.service.impl;

import com.matrimony.backend.entity.MembershipPlan;
import com.matrimony.backend.entity.User;
import com.matrimony.backend.enums.SubscriptionStatus;
import com.matrimony.backend.repository.UserSubscriptionRepository;
import com.matrimony.backend.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntitlementServiceImpl implements EntitlementService {
    private final UserSubscriptionRepository subscriptionRepository;

    @Override
    public int dailyInterestLimit(User user) {
        return plan(user).map(MembershipPlan::getDailyInterestLimit).orElse(5);
    }

    @Override
    public boolean canMessage(User user) {
        return plan(user).map(plan -> plan.getMessageLimit() > 0).orElse(false);
    }

    @Override
    public boolean canViewContact(User user) {
        return plan(user).map(plan -> plan.getContactViewLimit() > 0).orElse(false);
    }

    private java.util.Optional<MembershipPlan> plan(User user) {
        return subscriptionRepository.findFirstByUserIdAndStatusOrderByEndDateDesc(user.getId(), SubscriptionStatus.ACTIVE)
                .map(subscription -> subscription.getMembershipPlan());
    }
}
