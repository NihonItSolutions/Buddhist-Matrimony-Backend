package com.matrimony.backend.entity;

import com.matrimony.backend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_gateway_order", columnList = "gateway_order_id"),
        @Index(name = "idx_payment_gateway_payment", columnList = "gateway_payment_id")
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private UserSubscription subscription;
    private String paymentGateway;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 10)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.CREATED;
    @Column(precision = 12, scale = 2)
    private BigDecimal amountReceived;
    private String balanceUtr;
    @Column(length = 800)
    private String failureReason;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
