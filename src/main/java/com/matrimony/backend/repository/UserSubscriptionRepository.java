package com.matrimony.backend.repository;

import com.matrimony.backend.entity.UserSubscription;
import com.matrimony.backend.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findFirstByUserIdAndStatusOrderByEndDateDesc(Long userId, SubscriptionStatus status);

    Page<UserSubscription> findByUserId(Long userId, Pageable pageable);

    long countByStatus(SubscriptionStatus status);
}
