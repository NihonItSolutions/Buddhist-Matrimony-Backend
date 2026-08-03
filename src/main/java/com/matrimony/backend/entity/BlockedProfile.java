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
@Table(name = "blocked_profiles", uniqueConstraints =
@UniqueConstraint(name = "uk_block_pair", columnNames = {"blocked_by_profile_id", "blocked_profile_id"}))
public class BlockedProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blocked_by_profile_id", nullable = false)
    private MatrimonyProfile blockedByProfile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blocked_profile_id", nullable = false)
    private MatrimonyProfile blockedProfile;
    @Column(length = 500)
    private String reason;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
