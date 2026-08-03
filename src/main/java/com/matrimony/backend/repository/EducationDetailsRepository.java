package com.matrimony.backend.repository;

import com.matrimony.backend.entity.EducationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EducationDetailsRepository extends JpaRepository<EducationDetails, Long> {
    Optional<EducationDetails> findByProfileId(Long profileId);
}
