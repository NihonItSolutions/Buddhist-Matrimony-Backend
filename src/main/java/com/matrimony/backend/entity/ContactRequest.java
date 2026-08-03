package com.matrimony.backend.entity;

import com.matrimony.backend.enums.ContactRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "contact_requests")
public class ContactRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_profile_id", nullable = false)
    private MatrimonyProfile requesterProfile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_profile_id", nullable = false)
    private MatrimonyProfile receiverProfile;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContactRequestStatus status = ContactRequestStatus.PENDING;
    @Column(nullable = false)
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
}
