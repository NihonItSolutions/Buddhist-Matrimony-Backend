package com.matrimony.backend.repository;

import com.matrimony.backend.entity.HoroscopeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoroscopeDetailsRepository extends JpaRepository<HoroscopeDetails, Long> {
    Optional<HoroscopeDetails> findByProfileId(Long profileId);
}
