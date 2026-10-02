package com.matrimony.backend.dto.response;

import com.matrimony.backend.entity.Payment;
import com.matrimony.backend.entity.UserSubscription;
import com.matrimony.backend.enums.PaymentStatus;
import com.matrimony.backend.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public final class PaymentResponses {
    private PaymentResponses() {
    }

    public record OrderResponse(Long paymentId, String gatewayOrderId, String paymentSessionId, String planCode, BigDecimal amount, String currency, String environment, String upiId, String upiPayeeName) {
    }

    public record PaymentResponse(Long id, String gatewayOrderId, String gatewayPaymentId, BigDecimal amount, String currency, PaymentStatus status, LocalDateTime createdAt, LocalDateTime paidAt,
                                  BigDecimal amountReceived, BigDecimal balanceDue, String balanceUtr, String statusMessage, String paymentGateway) {
        public static PaymentResponse from(Payment payment) {
            BigDecimal balanceDue = null;
            if (payment.getAmountReceived() != null && payment.getAmountReceived().compareTo(payment.getAmount()) < 0) {
                balanceDue = payment.getAmount().subtract(payment.getAmountReceived());
            }
            return new PaymentResponse(payment.getId(), payment.getGatewayOrderId(), payment.getGatewayPaymentId(), payment.getAmount(), payment.getCurrency(), payment.getStatus(),
                    payment.getCreatedAt(), payment.getPaidAt(), payment.getAmountReceived(), balanceDue, payment.getBalanceUtr(), payment.getFailureReason(), payment.getPaymentGateway());
        }
    }

    public record UpiInfoResponse(String upiId, String payeeName) {
    }

    public record SubscriptionResponse(Long id, String planCode, SubscriptionStatus status, LocalDateTime startDate, LocalDateTime endDate, Integer remainingContactViews, Integer remainingMessages, Integer remainingInterestsToday, Long daysRemaining) {
        public static SubscriptionResponse from(UserSubscription subscription) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDate = subscription.getEndDate();
            SubscriptionStatus status = subscription.getStatus();
            if (status == SubscriptionStatus.ACTIVE && endDate != null && !endDate.isAfter(now)) {
                status = SubscriptionStatus.EXPIRED;
            }
            Long daysRemaining = null;
            if (endDate != null) {
                // Round up so a plan with a few hours left shows "1 day" rather than "0 days".
                long hoursLeft = Math.max(0, Duration.between(now, endDate).toHours());
                daysRemaining = status == SubscriptionStatus.ACTIVE ? (hoursLeft + 23) / 24 : 0L;
            }
            return new SubscriptionResponse(subscription.getId(), subscription.getMembershipPlan().getCode(), status, subscription.getStartDate(), endDate,
                    subscription.getRemainingContactViews(), subscription.getRemainingMessages(), subscription.getRemainingInterestsToday(), daysRemaining);
        }
    }
}
