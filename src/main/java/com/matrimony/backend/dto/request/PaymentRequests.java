package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public final class PaymentRequests {
    private PaymentRequests() {
    }

    public record CreateOrderRequest(@NotBlank String planCode) {
    }

    public record VerifyPaymentRequest(
            @NotBlank String gatewayOrderId,
            @NotBlank String gatewayPaymentId,
            @NotBlank String signature
    ) {
    }

    public record WebhookRequest(
            @NotBlank String eventId,
            @NotBlank String eventType,
            @NotBlank String gatewayOrderId,
            String gatewayPaymentId,
            String signature
    ) {
    }
}
