package com.matrimony.backend.repository;

import com.matrimony.backend.entity.FamilyDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyDetailsRepository extends JpaRepository<FamilyDetails, Long> {
    Optional<FamilyDetails> findByProfileId(Long profileId);
}
