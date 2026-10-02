package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.entity.Payment;
import com.matrimony.backend.service.PaymentGatewayService;
import com.matrimony.backend.util.HashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.payment.gateway", havingValue = "mock", matchIfMissing = true)
public class MockPaymentGatewayService implements PaymentGatewayService {
    private final AppProperties properties;

    @Override
    public String createOrder(Payment payment) {
        return "mock_order_" + UUID.randomUUID();
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        return HashingUtil.sha256(orderId + "|" + paymentId + "|" + properties.payment().signatureSecret()).equals(signature);
    }

    @Override
    public boolean verifyWebhook(String payloadId, String signature) {
        return HashingUtil.sha256(payloadId + "|" + properties.payment().webhookSecret()).equals(signature);
    }
}
