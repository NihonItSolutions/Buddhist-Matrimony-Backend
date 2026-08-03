package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "family_details", uniqueConstraints = @UniqueConstraint(name = "uk_family_profile", columnNames = "profile_id"))
public class FamilyDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;
    private String familyType;
    private String familyStatus;
    private String familyValues;
    private String fatherStatus;
    private String fatherOccupation;
    private String motherStatus;
    private String motherOccupation;
    private Integer numberOfBrothers;
    private Integer marriedBrothers;
    private Integer numberOfSisters;
    private Integer marriedSisters;
    private String familyCountry;
    private String familyState;
    private String familyCity;
    @Column(length = 1200)
    private String familyDescription;
}
