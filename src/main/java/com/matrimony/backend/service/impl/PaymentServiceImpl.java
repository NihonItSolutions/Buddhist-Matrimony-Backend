package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.PaymentRequests;
import com.matrimony.backend.dto.response.PaymentResponses.OrderResponse;
import com.matrimony.backend.dto.response.PaymentResponses.PaymentResponse;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.PaymentStatus;
import com.matrimony.backend.enums.SubscriptionStatus;
import com.matrimony.backend.exception.ForbiddenOperationException;
import com.matrimony.backend.exception.InvalidRequestException;
import com.matrimony.backend.exception.PaymentVerificationException;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.service.PaymentGatewayService;
import com.matrimony.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final AppProperties properties;
    private final CurrentUser currentUser;
    private final MembershipPlanRepository planRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentWebhookEventRepository webhookEventRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final PaymentNotifier paymentNotifier;

    @Override
    @Transactional
    public OrderResponse createOrder(PaymentRequests.CreateOrderRequest request) {
        MembershipPlan plan = planRepository.findByCode(request.planCode()).orElseThrow(() -> new ResourceNotFoundException("Membership plan not found"));
        String gateway = properties.payment().gateway().toUpperCase();
        if ("UPI".equals(gateway)) {
            // Re-opening checkout should show the same unpaid order, not create a new one each time.
            Payment open = paymentRepository
                    .findFirstByUserIdAndStatusAndPaymentGatewayAndGatewayPaymentIdIsNullOrderByIdDesc(currentUser.get().getId(), PaymentStatus.PENDING, gateway)
                    .filter(existing -> existing.getSubscription() != null && existing.getSubscription().getMembershipPlan().getId().equals(plan.getId()))
                    .orElse(null);
            if (open != null) {
                return toOrder(open, open.getGatewayOrderId(), open.getGatewayOrderId(), plan);
            }
        }
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
        payment.setPaymentGateway(properties.payment().gateway().toUpperCase());
        payment.setAmount(plan.getPrice());
        payment.setCurrency(plan.getCurrency());
        String rawOrderResult = paymentGatewayService.createOrder(payment);
        String gatewayOrderId = rawOrderResult;
        String paymentSessionId = rawOrderResult;
        if (rawOrderResult != null && rawOrderResult.contains(":")) {
            String[] parts = rawOrderResult.split(":", 2);
            gatewayOrderId = parts[0];
            paymentSessionId = parts[1];
        }
        payment.setGatewayOrderId(gatewayOrderId);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        return toOrder(payment, gatewayOrderId, paymentSessionId, plan);
    }

    private OrderResponse toOrder(Payment payment, String gatewayOrderId, String paymentSessionId, MembershipPlan plan) {
        String env = properties.payment().cashfree() != null ? properties.payment().cashfree().env() : "sandbox";
        AppProperties.Payment.Upi upi = properties.payment().upi();
        return new OrderResponse(payment.getId(), gatewayOrderId, paymentSessionId, plan.getCode(), payment.getAmount(), payment.getCurrency(), env,
                upi != null ? upi.vpa() : null, upi != null ? upi.payeeName() : null);
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
    public PaymentResponse submitUpiReference(Long paymentId, PaymentRequests.UpiReferenceRequest request) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!payment.getUser().getId().equals(currentUser.get().getId())) {
            throw new ForbiddenOperationException("Payment unavailable");
        }
        boolean balancePayment = payment.getStatus() == PaymentStatus.UNDERPAID;
        if (!"UPI".equals(payment.getPaymentGateway()) || (payment.getStatus() != PaymentStatus.PENDING && !balancePayment)) {
            throw new InvalidRequestException("This payment cannot accept a UPI reference");
        }
        boolean usedElsewhere = paymentRepository.findByGatewayPaymentId(request.utr())
                .filter(existing -> !existing.getId().equals(payment.getId()))
                .isPresent();
        if (usedElsewhere || paymentRepository.existsByBalanceUtr(request.utr()) || (balancePayment && request.utr().equals(payment.getGatewayPaymentId()))) {
            throw new InvalidRequestException("This UTR has already been submitted");
        }
        if (balancePayment) {
            // User paid the remaining amount; send it back to admin for re-verification.
            payment.setBalanceUtr(request.utr());
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            payment.setGatewayPaymentId(request.utr());
        }
        paymentNotifier.utrSubmitted(payment, request.utr(), balancePayment);
        return toPayment(payment);
    }

    @Override
    @Transactional
    public PaymentResponse verifyManualPayment(Long paymentId, BigDecimal amountReceived) {
        Payment payment = findPendingUpiPayment(paymentId);
        if (payment.getGatewayPaymentId() == null) {
            throw new InvalidRequestException("User has not submitted a UTR for this payment yet");
        }
        payment.setAmountReceived(amountReceived);
        if (amountReceived.compareTo(payment.getAmount()) < 0) {
            BigDecimal balance = payment.getAmount().subtract(amountReceived);
            payment.setStatus(PaymentStatus.UNDERPAID);
            payment.setFailureReason("We received " + PaymentNotifier.money(amountReceived, payment.getCurrency()) + " but the plan price is "
                    + PaymentNotifier.money(payment.getAmount(), payment.getCurrency()) + ". Please pay the remaining "
                    + PaymentNotifier.money(balance, payment.getCurrency()) + " and submit its UTR to activate your plan.");
            paymentNotifier.underpaid(payment, balance);
            return toPayment(payment);
        }
        payment.setFailureReason(null);
        activate(payment, payment.getGatewayPaymentId(), "ADMIN_APPROVED");
        paymentNotifier.activated(payment);
        return toPayment(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public long pendingVerificationCount() {
        return paymentRepository.countByStatusAndPaymentGatewayAndGatewayPaymentIdIsNotNull(PaymentStatus.PENDING, "UPI");
    }

    @Override
    public com.matrimony.backend.dto.response.PaymentResponses.UpiInfoResponse upiInfo() {
        AppProperties.Payment.Upi upi = properties.payment().upi();
        return new com.matrimony.backend.dto.response.PaymentResponses.UpiInfoResponse(upi != null ? upi.vpa() : null, upi != null ? upi.payeeName() : null);
    }

    @Override
    @Transactional
    public PaymentResponse rejectManualPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!"UPI".equals(payment.getPaymentGateway()) || (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.UNDERPAID)) {
            throw new InvalidRequestException("Only pending or underpaid UPI payments can be rejected");
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason == null || reason.isBlank() ? "Payment could not be verified" : reason);
        paymentNotifier.rejected(payment);
        UserSubscription subscription = payment.getSubscription();
        if (subscription != null && subscription.getStatus() == SubscriptionStatus.PENDING) {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
        }
        return toPayment(payment);
    }

    private Payment findPendingUpiPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!"UPI".equals(payment.getPaymentGateway()) || payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidRequestException("Only pending UPI payments can be approved or rejected");
        }
        return payment;
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
        return PaymentResponse.from(payment);
    }
}
