package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.entity.Notification;
import com.matrimony.backend.entity.Payment;
import com.matrimony.backend.entity.User;
import com.matrimony.backend.entity.UserSubscription;
import com.matrimony.backend.enums.NotificationType;
import com.matrimony.backend.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Tells the user what happened to their UPI payment: an in-app notification plus an email.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentNotifier {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final AppProperties properties;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void activated(Payment payment) {
        UserSubscription subscription = payment.getSubscription();
        String plan = subscription.getMembershipPlan().getName();
        String message = "Your payment of " + money(payment.getAmountReceived(), payment.getCurrency()) + " is verified. Your " + plan
                + " membership is now active until " + subscription.getEndDate().format(DATE) + " ("
                + subscription.getMembershipPlan().getDurationDays() + " days).";
        send(payment, NotificationType.PAYMENT_APPROVED, "Membership activated \uD83C\uDF89", message);
    }

    public void underpaid(Payment payment, BigDecimal balance) {
        String message = "We received " + money(payment.getAmountReceived(), payment.getCurrency()) + " but the "
                + payment.getSubscription().getMembershipPlan().getName() + " plan costs " + money(payment.getAmount(), payment.getCurrency())
                + ". Your plan is NOT active yet. Please pay the remaining " + money(balance, payment.getCurrency())
                + " and submit its UTR from My Payments.";
        send(payment, NotificationType.PAYMENT_UNDERPAID, "Payment incomplete: " + money(balance, payment.getCurrency()) + " due", message);
    }

    public void rejected(Payment payment) {
        String message = "We could not verify your payment (ref " + payment.getGatewayOrderId() + "). Reason: " + payment.getFailureReason()
                + ". Please contact support if you have already paid.";
        send(payment, NotificationType.PAYMENT_REJECTED, "Payment not verified", message);
    }

    public void utrSubmitted(Payment payment, String utr, boolean balancePayment) {
        AppProperties.Payment.Upi upi = properties.payment().upi();
        String to = upi != null ? upi.adminAlertEmail() : null;
        if (to == null || to.isBlank()) {
            return;
        }
        User user = payment.getUser();
        String subject = (balancePayment ? "Balance UTR submitted" : "New UPI payment to verify") + ": " + money(payment.getAmount(), payment.getCurrency());
        String message = "User " + user.getMatrimonyId() + " (" + user.getEmail() + ") submitted UTR " + utr + " for the "
                + payment.getSubscription().getMembershipPlan().getName() + " plan (" + money(payment.getAmount(), payment.getCurrency()) + "), payment ref "
                + payment.getGatewayOrderId() + ". Check your bank / UPI app for this UTR, then open Admin > Payments and click Verify.";
        afterCommit(() -> sendEmail(to, subject, message));
    }

    static String money(BigDecimal amount, String currency) {
        String value = amount.stripTrailingZeros().toPlainString();
        return "INR".equalsIgnoreCase(currency) ? "\u20B9" + value : value + " " + currency;
    }

    private void send(Payment payment, NotificationType type, String title, String message) {
        User user = payment.getUser();
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setNotificationType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceType("PAYMENT");
        notification.setReferenceId(payment.getId());
        notificationRepository.save(notification);

        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            return;
        }
        afterCommit(() -> sendEmail(email, title, message));
    }

    // Only email once the database change is committed, so nobody gets a message for a rolled-back update.
    private void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }

    private void sendEmail(String to, String subject, String message) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(fromEmail, "Buddhist Matrimony");
            helper.setTo(to);
            helper.setSubject(subject + " - Buddhist Matrimony");
            helper.setText("<div style=\"font-family:'Segoe UI',Arial,sans-serif;max-width:560px;margin:auto;padding:24px;\">"
                    + "<h2 style=\"color:#0f2b6f;\">" + HtmlUtils.htmlEscape(subject) + "</h2>"
                    + "<p style=\"font-size:15px;line-height:1.6;color:#334155;\">" + HtmlUtils.htmlEscape(message) + "</p>"
                    + "<p style=\"color:#64748b;font-size:13px;\">Buddhist Matrimony</p></div>", true);
            mailSender.send(mime);
        } catch (Exception e) {
            log.error("Failed to send payment email to {}", to, e);
        }
    }
}
