package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_webhook_events", uniqueConstraints = @UniqueConstraint(name = "uk_webhook_event", columnNames = "event_id"))
public class PaymentWebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String eventId;
    @Column(nullable = false)
    private String gateway;
    @Column(nullable = false, length = 80)
    private String eventType;
    @Column(nullable = false)
    private boolean processed;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
