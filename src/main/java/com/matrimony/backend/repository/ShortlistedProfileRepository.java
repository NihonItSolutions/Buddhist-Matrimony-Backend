package com.matrimony.backend.repository;

import com.matrimony.backend.entity.ShortlistedProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortlistedProfileRepository extends JpaRepository<ShortlistedProfile, Long> {
    Optional<ShortlistedProfile> findByOwnerProfileIdAndShortlistedProfileId(Long ownerId, Long shortlistedId);

    Page<ShortlistedProfile> findByOwnerProfileId(Long ownerId, Pageable pageable);

    boolean existsByOwnerProfileIdAndShortlistedProfileId(Long ownerId, Long shortlistedId);
}
