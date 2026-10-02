package com.matrimony.backend.repository;

import com.matrimony.backend.entity.UserSubscription;
import com.matrimony.backend.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findFirstByUserIdAndStatusOrderByEndDateDesc(Long userId, SubscriptionStatus status);

    // A paid plan always takes priority over the long-running FREE plan, so rank by plan price first.
    @Query("select s from UserSubscription s where s.user.id = :userId and s.status = com.matrimony.backend.enums.SubscriptionStatus.ACTIVE"
            + " and s.endDate > :now order by s.membershipPlan.price desc, s.endDate desc")
    List<UserSubscription> findActiveRankedByPlanPrice(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    default Optional<UserSubscription> findCurrentActive(Long userId) {
        return findActiveRankedByPlanPrice(userId, LocalDateTime.now()).stream().findFirst();
    }

    @Modifying
    @Query("update UserSubscription s set s.status = com.matrimony.backend.enums.SubscriptionStatus.EXPIRED where s.status = com.matrimony.backend.enums.SubscriptionStatus.ACTIVE and s.endDate <= :now")
    int expireEnded(@Param("now") LocalDateTime now);

    Page<UserSubscription> findByUserId(Long userId, Pageable pageable);

    long countByStatus(SubscriptionStatus status);
}
