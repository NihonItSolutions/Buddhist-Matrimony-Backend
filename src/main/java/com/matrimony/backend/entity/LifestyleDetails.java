package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lifestyle_details", uniqueConstraints = @UniqueConstraint(name = "uk_lifestyle_profile", columnNames = "profile_id"))
public class LifestyleDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;
    private String diet;
    private String smokingHabit;
    private String drinkingHabit;
    @Column(length = 1000)
    private String hobbies;
    @Column(length = 1000)
    private String interests;
    @Column(length = 1000)
    private String languagesKnown;
}
