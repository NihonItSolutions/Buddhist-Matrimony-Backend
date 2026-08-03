package com.matrimony.backend.repository;

import com.matrimony.backend.entity.BlockedProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedProfileRepository extends JpaRepository<BlockedProfile, Long> {
    Optional<BlockedProfile> findByBlockedByProfileIdAndBlockedProfileId(Long blockedById, Long blockedId);

    boolean existsByBlockedByProfileIdAndBlockedProfileId(Long blockedById, Long blockedId);

    boolean existsByBlockedByProfileIdAndBlockedProfileIdOrBlockedByProfileIdAndBlockedProfileId(Long a, Long b, Long c, Long d);

    Page<BlockedProfile> findByBlockedByProfileId(Long blockedById, Pageable pageable);
}
