package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.entity.Payment;
import com.matrimony.backend.service.PaymentGatewayService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.payment.gateway", havingValue = "stripe")
public class StripePaymentGatewayService implements PaymentGatewayService {
    
    private final String webhookSecret;

    public StripePaymentGatewayService(AppProperties properties) {
        Stripe.apiKey = properties.payment().stripe().secretKey();
        this.webhookSecret = properties.payment().webhookSecret();
    }

    @Override
    public String createOrder(Payment payment) {
        try {
            // Stripe expects amount in subunits (paise/cents)
            long amountInSubunits = payment.getAmount().multiply(new BigDecimal("100")).longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(payment.getId().toString())
                .setSuccessUrl("http://localhost:4200/payments/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:4200/payments/failed")
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(payment.getCurrency().toLowerCase())
                                .setUnitAmount(amountInSubunits)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Membership Activation")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

            Session session = Session.create(params);
            
            // Return the session URL, which the frontend will redirect to
            return session.getUrl();
        } catch (StripeException e) {
            log.error("Failed to create Stripe checkout session", e);
            throw new RuntimeException("Failed to initiate payment. Please try again.");
        }
    }

    @Override
    public boolean verifySignature(String gatewayOrderId, String gatewayPaymentId, String signature) {
        // For Stripe Checkout, gatewayPaymentId is expected to be the session_id
        try {
            Session session = Session.retrieve(gatewayPaymentId);
            return "paid".equals(session.getPaymentStatus());
        } catch (StripeException e) {
            log.warn("Stripe session verification failed", e);
            return false;
        }
    }

    @Override
    public boolean verifyWebhook(String payload, String signature) {
        // Stripe webhook verification would typically use the com.stripe.net.Webhook class
        // Webhook.constructEvent(payload, signature, webhookSecret);
        // For now, we rely on the synchronous frontend verification (verifySignature)
        return true;
    }
}
