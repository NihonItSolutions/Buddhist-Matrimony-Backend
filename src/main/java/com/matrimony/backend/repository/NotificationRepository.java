package com.matrimony.backend.repository;

import com.matrimony.backend.entity.Notification;
import com.matrimony.backend.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndNotificationType(Long userId, NotificationType type, Pageable pageable);

    long countByUserIdAndReadFalse(Long userId);
}
