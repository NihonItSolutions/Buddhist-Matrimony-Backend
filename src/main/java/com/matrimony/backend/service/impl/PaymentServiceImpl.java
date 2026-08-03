package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.PaymentRequests;
import com.matrimony.backend.dto.response.PaymentResponses.OrderResponse;
import com.matrimony.backend.dto.response.PaymentResponses.PaymentResponse;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.PaymentStatus;
import com.matrimony.backend.enums.SubscriptionStatus;
import com.matrimony.backend.exception.ForbiddenOperationException;
import com.matrimony.backend.exception.PaymentVerificationException;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.PaymentGatewayService;
import com.matrimony.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final CurrentUser currentUser;
    private final MembershipPlanRepository planRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentWebhookEventRepository webhookEventRepository;
    private final PaymentGatewayService paymentGatewayService;

    @Override
    @Transactional
    public OrderResponse createOrder(PaymentRequests.CreateOrderRequest request) {
        MembershipPlan plan = planRepository.findByCode(request.planCode()).orElseThrow(() -> new ResourceNotFoundException("Membership plan not found"));
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(currentUser.get());
        subscription.setMembershipPlan(plan);
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setRemainingContactViews(plan.getContactViewLimit());
        subscription.setRemainingMessages(plan.getMessageLimit());
        subscription.setRemainingInterestsToday(plan.getDailyInterestLimit());
        subscriptionRepository.save(subscription);

        Payment payment = new Payment();
        payment.setUser(currentUser.get());
        payment.setSubscription(subscription);
        payment.setPaymentGateway("MOCK");
        payment.setAmount(plan.getPrice());
        payment.setCurrency(plan.getCurrency());
        String orderId = paymentGatewayService.createOrder(payment);
        payment.setGatewayOrderId(orderId);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        return new OrderResponse(payment.getId(), orderId, plan.getCode(), payment.getAmount(), payment.getCurrency());
    }

    @Override
    @Transactional
    public PaymentResponse verify(PaymentRequests.VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByGatewayOrderId(request.gatewayOrderId()).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!payment.getUser().getId().equals(currentUser.get().getId())) {
            throw new ForbiddenOperationException("Payment unavailable");
        }
        if (!paymentGatewayService.verifySignature(request.gatewayOrderId(), request.gatewayPaymentId(), request.signature())) {
            throw new PaymentVerificationException("Payment signature verification failed");
        }
        activate(payment, request.gatewayPaymentId(), request.signature());
        return toPayment(payment);
    }

    @Override
    @Transactional
    public void webhook(PaymentRequests.WebhookRequest request, String signature) {
        if (webhookEventRepository.existsByEventId(request.eventId())) {
            return;
        }
        if (!paymentGatewayService.verifyWebhook(request.eventId(), signature == null ? request.signature() : signature)) {
            throw new PaymentVerificationException("Webhook signature verification failed");
        }
        PaymentWebhookEvent event = new PaymentWebhookEvent();
        event.setEventId(request.eventId());
        event.setGateway("MOCK");
        event.setEventType(request.eventType());
        webhookEventRepository.save(event);
        if ("payment.success".equalsIgnoreCase(request.eventType())) {
            paymentRepository.findByGatewayOrderId(request.gatewayOrderId())
                    .ifPresent(payment -> activate(payment, request.gatewayPaymentId(), request.signature()));
        }
        event.setProcessed(true);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> myPayments(Pageable pageable) {
        return PageResponse.from(paymentRepository.findByUserId(currentUser.get().getId(), pageable).map(this::toPayment));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse myPayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!payment.getUser().getId().equals(currentUser.get().getId())) {
            throw new ForbiddenOperationException("Payment unavailable");
        }
        return toPayment(payment);
    }

    private void activate(Payment payment, String gatewayPaymentId, String signature) {
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }
        payment.setGatewayPaymentId(gatewayPaymentId);
        payment.setGatewaySignature(signature);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        UserSubscription subscription = payment.getSubscription();
        if (subscription != null && subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            LocalDateTime now = LocalDateTime.now();
            subscription.setStartDate(now);
            subscription.setEndDate(now.plusDays(subscription.getMembershipPlan().getDurationDays()));
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
    }

    private PaymentResponse toPayment(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getGatewayOrderId(), payment.getGatewayPaymentId(), payment.getAmount(), payment.getCurrency(), payment.getStatus(), payment.getCreatedAt(), payment.getPaidAt());
    }
}
