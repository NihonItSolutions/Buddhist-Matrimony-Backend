package com.matrimony.backend.dto.response;

import com.matrimony.backend.enums.PaymentStatus;
import com.matrimony.backend.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PaymentResponses {
    private PaymentResponses() {
    }

    public record OrderResponse(Long paymentId, String gatewayOrderId, String planCode, BigDecimal amount, String currency) {
    }

    public record PaymentResponse(Long id, String gatewayOrderId, String gatewayPaymentId, BigDecimal amount, String currency, PaymentStatus status, LocalDateTime createdAt, LocalDateTime paidAt) {
    }

    public record SubscriptionResponse(Long id, String planCode, SubscriptionStatus status, LocalDateTime startDate, LocalDateTime endDate, Integer remainingContactViews, Integer remainingMessages, Integer remainingInterestsToday) {
    }
}
