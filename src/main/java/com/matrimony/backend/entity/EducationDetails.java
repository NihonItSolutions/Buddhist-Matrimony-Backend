package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "education_details", uniqueConstraints = @UniqueConstraint(name = "uk_education_profile", columnNames = "profile_id"))
public class EducationDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;

    @Column(name = "highest_education", length = 120)
    private String highestEducation;
    @Column(name = "education_specialization", length = 120)
    private String educationSpecialization;
    @Column(name = "college_name", length = 180)
    private String collegeName;
    @Column(name = "additional_qualification", length = 180)
    private String additionalQualification;
}
