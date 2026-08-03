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
@Table(name = "shortlisted_profiles", uniqueConstraints =
@UniqueConstraint(name = "uk_shortlist_pair", columnNames = {"owner_profile_id", "shortlisted_profile_id"}))
public class ShortlistedProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_profile_id", nullable = false)
    private MatrimonyProfile ownerProfile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shortlisted_profile_id", nullable = false)
    private MatrimonyProfile shortlistedProfile;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
