package com.matrimony.backend.entity;

import com.matrimony.backend.enums.Gender;
import com.matrimony.backend.enums.MaritalStatus;
import com.matrimony.backend.enums.ProfileCreatedFor;
import com.matrimony.backend.enums.ProfileStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "matrimony_profiles", indexes = {
        @Index(name = "idx_profile_gender", columnList = "gender"),
        @Index(name = "idx_profile_dob", columnList = "date_of_birth"),
        @Index(name = "idx_profile_religion", columnList = "religion"),
        @Index(name = "idx_profile_community", columnList = "community"),
        @Index(name = "idx_profile_state", columnList = "state"),
        @Index(name = "idx_profile_city", columnList = "city"),
        @Index(name = "idx_profile_status", columnList = "profile_status"),
        @Index(name = "idx_profile_last_active", columnList = "last_active_at")
}, uniqueConstraints = @UniqueConstraint(name = "uk_profile_user", columnNames = "user_id"))
public class MatrimonyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_created_for", nullable = false, length = 30)
    private ProfileCreatedFor profileCreatedFor;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "middle_name", length = 80)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "height_in_cm")
    private Integer heightInCm;

    @Column(name = "weight_in_kg")
    private Integer weightInKg;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 40)
    private MaritalStatus maritalStatus;

    @Column(name = "number_of_children")
    private Integer numberOfChildren;

    @Column(name = "children_living_status", length = 80)
    private String childrenLivingStatus;

    @Column(name = "physical_status", length = 80)
    private String physicalStatus;

    @Column(name = "mother_tongue", length = 80)
    private String motherTongue;

    @Column(length = 80)
    private String religion;

    @Column(length = 80)
    private String community;

    @Column(name = "sub_community", length = 80)
    private String subCommunity;

    @Column(name = "caste_no_bar")
    private Boolean casteNoBar;

    @Column(length = 80)
    private String gothra;

    @Column(name = "manglik_status", length = 80)
    private String manglikStatus;

    @Column(length = 80)
    private String citizenship;

    @Column(length = 80)
    private String country;

    @Column(length = 80)
    private String state;

    @Column(length = 80)
    private String district;

    @Column(length = 80)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "residency_status", length = 80)
    private String residencyStatus;

    @Column(name = "leaving_certificate_url")
    private String leavingCertificateUrl;

    @Column(name = "aadhar_card_url")
    private String aadharCardUrl;

    @Column(name = "documents_verified", nullable = false)
    private boolean documentsVerified = false;

    @Column(name = "about_me", length = 2000)
    private String aboutMe;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_status", nullable = false, length = 40)
    private ProfileStatus profileStatus = ProfileStatus.DRAFT;

    @Column(name = "profile_completeness", nullable = false)
    private int profileCompleteness;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
