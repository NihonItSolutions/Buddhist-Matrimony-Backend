package com.matrimony.backend.service.impl;

import com.matrimony.backend.entity.Payment;
import com.matrimony.backend.service.PaymentGatewayService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Direct UPI payments to the business UPI ID / QR code. There is no gateway callback,
 * so payments are never self-verified: the user submits a UTR and an admin approves it.
 */
@Service
@ConditionalOnProperty(name = "app.payment.gateway", havingValue = "upi")
public class UpiPaymentGatewayService implements PaymentGatewayService {

    @Override
    public String createOrder(Payment payment) {
        return "BM" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        return false;
    }

    @Override
    public boolean verifyWebhook(String payloadId, String signature) {
        return false;
    }
}
