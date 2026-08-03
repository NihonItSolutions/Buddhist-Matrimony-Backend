package com.matrimony.backend.entity;

import com.matrimony.backend.enums.SuccessStoryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "success_stories")
public class SuccessStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bride_profile_id")
    private MatrimonyProfile brideProfile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groom_profile_id")
    private MatrimonyProfile groomProfile;
    private String brideName;
    private String groomName;
    @Column(nullable = false, length = 3000)
    private String story;
    private LocalDate marriageDate;
    private String photoUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SuccessStoryStatus status = SuccessStoryStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}
