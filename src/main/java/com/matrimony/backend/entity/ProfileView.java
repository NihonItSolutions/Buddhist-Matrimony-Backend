package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profile_views", indexes = {
        @Index(name = "idx_profile_view_viewer", columnList = "viewer_profile_id"),
        @Index(name = "idx_profile_view_viewed", columnList = "viewed_profile_id")
})
public class ProfileView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viewer_profile_id", nullable = false)
    private MatrimonyProfile viewerProfile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viewed_profile_id", nullable = false)
    private MatrimonyProfile viewedProfile;
    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
}
