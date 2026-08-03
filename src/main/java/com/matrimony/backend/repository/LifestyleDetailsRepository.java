package com.matrimony.backend.repository;

import com.matrimony.backend.entity.LifestyleDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LifestyleDetailsRepository extends JpaRepository<LifestyleDetails, Long> {
    Optional<LifestyleDetails> findByProfileId(Long profileId);
}
