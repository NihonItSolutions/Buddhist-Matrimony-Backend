package com.matrimony.backend.repository;

import com.matrimony.backend.entity.OtpToken;
import com.matrimony.backend.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findFirstByDestinationAndPurposeOrderByCreatedAtDesc(String destination, OtpPurpose purpose);
}
