package com.matrimony.backend.service;

import com.matrimony.backend.entity.Payment;

public interface PaymentGatewayService {
    String createOrder(Payment payment);

    boolean verifySignature(String orderId, String paymentId, String signature);

    boolean verifyWebhook(String payloadId, String signature);
}
