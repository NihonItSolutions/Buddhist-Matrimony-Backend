package com.matrimony.backend.repository;

import com.matrimony.backend.entity.Interest;
import com.matrimony.backend.enums.InterestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    @Query("select i from Interest i where ((i.senderProfile.id = :a and i.receiverProfile.id = :b) or (i.senderProfile.id = :b and i.receiverProfile.id = :a)) and i.status in :statuses")
    Optional<Interest> findActiveBetween(@Param("a") Long a, @Param("b") Long b, @Param("statuses") Collection<InterestStatus> statuses);

    boolean existsBySenderProfileIdAndSentAtBetween(Long senderProfileId, LocalDateTime from, LocalDateTime to);

    long countBySenderProfileIdAndSentAtBetween(Long senderProfileId, LocalDateTime from, LocalDateTime to);

    long countBySentAtBetween(LocalDateTime from, LocalDateTime to);

    Page<Interest> findBySenderProfileId(Long senderProfileId, Pageable pageable);

    Page<Interest> findByReceiverProfileId(Long receiverProfileId, Pageable pageable);

    Page<Interest> findBySenderProfileIdOrReceiverProfileIdAndStatus(Long senderProfileId, Long receiverProfileId, InterestStatus status, Pageable pageable);
}
