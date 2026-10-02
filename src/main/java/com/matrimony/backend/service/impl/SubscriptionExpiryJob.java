package com.matrimony.backend.service.impl;

import com.matrimony.backend.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpiryJob {
    private final UserSubscriptionRepository subscriptionRepository;

    @Scheduled(fixedDelayString = "PT1H", initialDelayString = "PT1M")
    @Transactional
    public void expireEndedSubscriptions() {
        int expired = subscriptionRepository.expireEnded(LocalDateTime.now());
        if (expired > 0) {
            log.info("Marked {} subscription(s) as EXPIRED", expired);
        }
    }
}
