package com.matrimony.backend.entity;

import com.matrimony.backend.enums.ProfileVisibility;
import com.matrimony.backend.enums.VisibilityLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profile_privacy_settings", uniqueConstraints = @UniqueConstraint(name = "uk_privacy_profile", columnNames = "profile_id"))
public class ProfilePrivacySetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProfileVisibility profileVisibility = ProfileVisibility.ALL_REGISTERED_USERS;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VisibilityLevel photoVisibility = VisibilityLevel.REGISTERED_USERS;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VisibilityLevel contactVisibility = VisibilityLevel.ACCEPTED_MATCHES;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VisibilityLevel dateOfBirthVisibility = VisibilityLevel.PRIVATE;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VisibilityLevel incomeVisibility = VisibilityLevel.PRIVATE;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VisibilityLevel horoscopeVisibility = VisibilityLevel.PRIVATE;
    @Column(nullable = false)
    private boolean allowProfileViews = true;
    @Column(nullable = false)
    private boolean allowMessages = true;
    @Column(nullable = false)
    private boolean allowContactRequests = true;
    @Column(nullable = false)
    private boolean showOnlineStatus = true;
    @Column(nullable = false)
    private boolean showLastActive = true;
}
