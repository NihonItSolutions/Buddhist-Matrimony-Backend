package com.matrimony.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "membership_plans", uniqueConstraints = @UniqueConstraint(name = "uk_membership_plan_code", columnNames = "code"))
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 40)
    private String code;
    @Column(length = 1200)
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(nullable = false, length = 10)
    private String currency;
    @Column(nullable = false)
    private int durationDays;
    @Column(nullable = false)
    private int dailyInterestLimit;
    @Column(nullable = false)
    private int contactViewLimit;
    @Column(nullable = false)
    private int messageLimit;
    @Column(nullable = false)
    private boolean profileBoostAllowed;
    @Column(nullable = false)
    private boolean assistedService;
    @Column(nullable = false)
    private boolean active = true;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
