package com.matrimony.backend.service;

import com.matrimony.backend.entity.User;
import com.matrimony.backend.entity.UserSubscription;

import java.util.Optional;

public interface EntitlementService {
    int dailyInterestLimit(User user);

    boolean canMessage(User user);

    boolean canViewContact(User user);

    boolean hasActivePlan(User user);

    Optional<UserSubscription> activeSubscription(User user);
}
