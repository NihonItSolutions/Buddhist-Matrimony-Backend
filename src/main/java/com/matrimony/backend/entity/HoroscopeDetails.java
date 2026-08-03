package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "horoscope_details", uniqueConstraints = @UniqueConstraint(name = "uk_horoscope_profile", columnNames = "profile_id"))
public class HoroscopeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;
    private LocalDate dateOfBirth;
    private LocalTime timeOfBirth;
    private String placeOfBirth;
    private String rashi;
    private String nakshatra;
    private String manglikStatus;
    private Boolean horoscopeAvailable;
    private String horoscopeDocumentUrl;
}
