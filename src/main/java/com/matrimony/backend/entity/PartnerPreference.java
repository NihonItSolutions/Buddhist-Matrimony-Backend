package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "partner_preferences", uniqueConstraints = @UniqueConstraint(name = "uk_partner_preference_profile", columnNames = "profile_id"))
public class PartnerPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;
    private Integer minimumAge;
    private Integer maximumAge;
    private Integer minimumHeight;
    private Integer maximumHeight;
    @ElementCollection
    @CollectionTable(name = "partner_preference_marital_statuses", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> maritalStatuses = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_religions", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> religions = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_communities", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> communities = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_sub_communities", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> subCommunities = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_mother_tongues", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> motherTongues = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_countries", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> countries = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_states", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> states = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_cities", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> cities = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_education_levels", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> educationLevels = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_occupations", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> occupations = new ArrayList<>();
    private BigDecimal minimumIncome;
    private BigDecimal maximumIncome;
    @ElementCollection
    @CollectionTable(name = "partner_preference_diets", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> dietPreferences = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_physical_statuses", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> physicalStatusPreferences = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "partner_preference_manglik", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "preference_value")
    private List<String> manglikPreferences = new ArrayList<>();
    @Column(length = 1200)
    private String description;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
