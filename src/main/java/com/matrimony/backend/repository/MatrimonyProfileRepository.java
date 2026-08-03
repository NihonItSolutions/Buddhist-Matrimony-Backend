package com.matrimony.backend.repository;

import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.entity.User;
import com.matrimony.backend.enums.Gender;
import com.matrimony.backend.enums.ProfileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MatrimonyProfileRepository extends JpaRepository<MatrimonyProfile, Long>, JpaSpecificationExecutor<MatrimonyProfile> {
    Optional<MatrimonyProfile> findByUser(User user);

    Optional<MatrimonyProfile> findByUserId(Long userId);

    Optional<MatrimonyProfile> findByUserMatrimonyId(String matrimonyId);

    long countByProfileStatus(ProfileStatus status);

    long countByGender(Gender gender);

    Page<MatrimonyProfile> findByProfileStatus(ProfileStatus status, Pageable pageable);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
