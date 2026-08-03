package com.matrimony.backend.repository;

import com.matrimony.backend.entity.CareerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CareerDetailsRepository extends JpaRepository<CareerDetails, Long> {
    Optional<CareerDetails> findByProfileId(Long profileId);
}
