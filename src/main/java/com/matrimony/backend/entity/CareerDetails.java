package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "career_details", uniqueConstraints = @UniqueConstraint(name = "uk_career_profile", columnNames = "profile_id"))
public class CareerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private MatrimonyProfile profile;
    @Column(name = "employed_in", length = 80)
    private String employedIn;
    @Column(length = 120)
    private String occupation;
    @Column(name = "company_name", length = 180)
    private String companyName;
    @Column(name = "job_title", length = 120)
    private String jobTitle;
    @Column(name = "annual_income", precision = 14, scale = 2)
    private BigDecimal annualIncome;
    @Column(name = "income_currency", length = 10)
    private String incomeCurrency;
    @Column(name = "work_country", length = 80)
    private String workCountry;
    @Column(name = "work_state", length = 80)
    private String workState;
    @Column(name = "work_city", length = 80)
    private String workCity;
}
