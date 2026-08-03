package com.matrimony.backend.entity;

import com.matrimony.backend.enums.ReportReason;
import com.matrimony.backend.enums.ReportStatus;
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
@Table(name = "profile_reports")
public class ProfileReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_profile_id", nullable = false)
    private MatrimonyProfile reporterProfile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_profile_id", nullable = false)
    private MatrimonyProfile reportedProfile;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportReason reason;
    @Column(length = 1200)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ReportStatus status = ReportStatus.OPEN;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    private LocalDateTime reviewedAt;
    @Column(length = 1200)
    private String adminComment;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
