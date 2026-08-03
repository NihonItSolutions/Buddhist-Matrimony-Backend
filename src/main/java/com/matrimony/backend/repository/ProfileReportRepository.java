package com.matrimony.backend.repository;

import com.matrimony.backend.entity.ProfileReport;
import com.matrimony.backend.enums.ReportReason;
import com.matrimony.backend.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProfileReportRepository extends JpaRepository<ProfileReport, Long> {
    Optional<ProfileReport> findFirstByReporterProfileIdAndReportedProfileIdAndReasonAndCreatedAtAfter(Long reporterId, Long reportedId, ReportReason reason, LocalDateTime after);

    Page<ProfileReport> findByReporterProfileId(Long reporterId, Pageable pageable);

    Page<ProfileReport> findByStatus(ReportStatus status, Pageable pageable);
}
