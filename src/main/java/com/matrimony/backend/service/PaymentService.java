package com.matrimony.backend.service;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.PaymentRequests;
import com.matrimony.backend.dto.response.PaymentResponses.OrderResponse;
import com.matrimony.backend.dto.response.PaymentResponses.PaymentResponse;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    OrderResponse createOrder(PaymentRequests.CreateOrderRequest request);

    PaymentResponse verify(PaymentRequests.VerifyPaymentRequest request);

    PaymentResponse submitUpiReference(Long paymentId, PaymentRequests.UpiReferenceRequest request);

    PaymentResponse verifyManualPayment(Long paymentId, java.math.BigDecimal amountReceived);

    com.matrimony.backend.dto.response.PaymentResponses.UpiInfoResponse upiInfo();

    long pendingVerificationCount();

    PaymentResponse rejectManualPayment(Long paymentId, String reason);

    void webhook(PaymentRequests.WebhookRequest request, String signature);

    PageResponse<PaymentResponse> myPayments(Pageable pageable);

    PaymentResponse myPayment(Long id);
}
