package com.matrimony.backend.service;

import com.matrimony.backend.entity.User;

public interface EntitlementService {
    int dailyInterestLimit(User user);

    boolean canMessage(User user);

    boolean canViewContact(User user);
}
