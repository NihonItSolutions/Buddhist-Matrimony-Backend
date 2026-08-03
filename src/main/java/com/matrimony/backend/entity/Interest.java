package com.matrimony.backend.entity;

import com.matrimony.backend.enums.InterestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "interests", indexes = {
        @Index(name = "idx_interest_sender", columnList = "sender_profile_id"),
        @Index(name = "idx_interest_receiver", columnList = "receiver_profile_id"),
        @Index(name = "idx_interest_status", columnList = "status")
})
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_profile_id", nullable = false)
    private MatrimonyProfile senderProfile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_profile_id", nullable = false)
    private MatrimonyProfile receiverProfile;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InterestStatus status = InterestStatus.PENDING;
    @Column(length = 500)
    private String message;
    @Column(nullable = false)
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
    private LocalDateTime cancelledAt;
}
