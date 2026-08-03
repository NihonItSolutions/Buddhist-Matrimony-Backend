package com.matrimony.backend.repository;

import com.matrimony.backend.entity.ProfileView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProfileViewRepository extends JpaRepository<ProfileView, Long> {
    Optional<ProfileView> findFirstByViewerProfileIdAndViewedProfileIdAndViewedAtAfter(Long viewerId, Long viewedId, LocalDateTime after);

    Page<ProfileView> findByViewerProfileId(Long viewerProfileId, Pageable pageable);

    Page<ProfileView> findByViewedProfileId(Long viewedProfileId, Pageable pageable);
}
