package com.matrimony.backend.entity;

import com.matrimony.backend.enums.ModerationStatus;
import com.matrimony.backend.enums.PhotoPrivacy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profile_photos")
public class ProfilePhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;
    @Column(name = "storage_key", nullable = false)
    private String storageKey;
    @Column(name = "photo_url", nullable = false)
    private String photoUrl;
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    @Column(name = "primary_photo", nullable = false)
    private boolean primaryPhoto;
    @Column(name = "display_order", nullable = false)
    private int displayOrder;
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level", nullable = false, length = 40)
    private PhotoPrivacy privacyLevel = PhotoPrivacy.REGISTERED_USERS;
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, length = 40)
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;
    @Column(name = "rejection_reason", length = 800)
    private String rejectionReason;
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
    private LocalDateTime approvedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}
