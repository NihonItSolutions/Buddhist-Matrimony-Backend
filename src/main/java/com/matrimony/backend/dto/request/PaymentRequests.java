package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

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

    public record UpiReferenceRequest(
            @NotBlank @Pattern(regexp = "\\d{12}", message = "UTR must be the 12-digit UPI transaction reference") String utr
    ) {
    }

    public record ApprovePaymentRequest(@NotNull @Positive BigDecimal amountReceived) {
    }

    public record RejectPaymentRequest(String reason) {
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
