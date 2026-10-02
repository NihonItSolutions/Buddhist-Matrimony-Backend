package com.matrimony.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.entity.Payment;
import com.matrimony.backend.entity.User;
import com.matrimony.backend.service.PaymentGatewayService;
import com.matrimony.backend.util.HashingUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.payment.gateway", havingValue = "cashfree")
public class CashfreePaymentGatewayService implements PaymentGatewayService {
    private static final Logger log = LoggerFactory.getLogger(CashfreePaymentGatewayService.class);
    private final AppProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        String env = properties.payment().cashfree() != null ? properties.payment().cashfree().env() : "sandbox";
        if ("production".equalsIgnoreCase(env)) {
            return "https://api.cashfreepayments.com/pg";
        }
        return "https://sandbox.cashfreepayments.com/pg";
    }

    @Override
    public String createOrder(Payment payment) {
        try {
            String url = getBaseUrl() + "/orders";
            User user = payment.getUser();
            String orderId = "order_" + payment.getId() + "_" + System.currentTimeMillis();

            Map<String, Object> customerDetails = new HashMap<>();
            customerDetails.put("customer_id", "cust_" + (user != null ? user.getId() : "guest"));
            customerDetails.put("customer_name", "Customer " + (user != null ? user.getMatrimonyId() : ""));
            customerDetails.put("customer_email", user != null && user.getEmail() != null ? user.getEmail() : "user@example.com");
            customerDetails.put("customer_phone", user != null && user.getMobileNumber() != null ? user.getMobileNumber() : "9999999999");

            Map<String, Object> orderMeta = new HashMap<>();
            orderMeta.put("return_url", "http://localhost:4200/payments/result?order_id={order_id}");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("order_id", orderId);
            requestBody.put("order_amount", payment.getAmount());
            requestBody.put("order_currency", payment.getCurrency() != null ? payment.getCurrency() : "INR");
            requestBody.put("customer_details", customerDetails);
            requestBody.put("order_meta", orderMeta);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (properties.payment().cashfree() != null) {
                headers.set("x-client-id", properties.payment().cashfree().appId());
                headers.set("x-client-secret", properties.payment().cashfree().secretKey());
                headers.set("x-api-version", properties.payment().cashfree().apiVersion() != null ? properties.payment().cashfree().apiVersion() : "2023-08-01");
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String cfOrderId = root.has("order_id") ? root.get("order_id").asText() : orderId;
                String paymentSessionId = root.has("payment_session_id") ? root.get("payment_session_id").asText() : "";
                return cfOrderId + ":" + paymentSessionId;
            } else {
                throw new RuntimeException("Cashfree order creation returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Failed to create Cashfree order", e);
            throw new RuntimeException("Payment gateway order creation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String url = getBaseUrl() + "/orders/" + orderId;
            HttpHeaders headers = new HttpHeaders();
            if (properties.payment().cashfree() != null) {
                headers.set("x-client-id", properties.payment().cashfree().appId());
                headers.set("x-client-secret", properties.payment().cashfree().secretKey());
                headers.set("x-api-version", properties.payment().cashfree().apiVersion() != null ? properties.payment().cashfree().apiVersion() : "2023-08-01");
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String orderStatus = root.has("order_status") ? root.get("order_status").asText() : "";
                return "PAID".equalsIgnoreCase(orderStatus);
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to verify Cashfree order status", e);
            return false;
        }
    }

    @Override
    public boolean verifyWebhook(String payload, String signature) {
        try {
            String secret = properties.payment().webhookSecret();
            return HashingUtil.sha256(payload + "|" + secret).equals(signature);
        } catch (Exception e) {
            log.error("Failed to verify Cashfree webhook signature", e);
            return false;
        }
    }
}
