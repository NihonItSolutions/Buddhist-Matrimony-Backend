package com.matrimony.backend.service.impl;

import com.matrimony.backend.entity.User;
import com.matrimony.backend.entity.UserSubscription;
import com.matrimony.backend.enums.SubscriptionStatus;
import com.matrimony.backend.repository.UserSubscriptionRepository;
import com.matrimony.backend.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntitlementServiceImpl implements EntitlementService {
    private final UserSubscriptionRepository subscriptionRepository;

    @Override
    public int dailyInterestLimit(User user) {
        return activeSubscription(user)
                .map(sub -> sub.getMembershipPlan().getDailyInterestLimit())
                .orElse(5);
    }

    @Override
    public boolean canMessage(User user) {
        return activeSubscription(user)
                .map(sub -> sub.getRemainingMessages() != null && sub.getRemainingMessages() > 0)
                .orElse(false);
    }

    @Override
    public boolean canViewContact(User user) {
        return activeSubscription(user)
                .map(sub -> sub.getRemainingContactViews() != null && sub.getRemainingContactViews() > 0)
                .orElse(false);
    }

    @Override
    public boolean hasActivePlan(User user) {
        return activeSubscription(user).isPresent();
    }

    @Override
    public Optional<UserSubscription> activeSubscription(User user) {
        return subscriptionRepository.findCurrentActive(user.getId());
    }
}
