package com.matrimony.backend.repository;

import com.matrimony.backend.entity.Interest;
import com.matrimony.backend.enums.InterestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    default Optional<Interest> findActiveBetween(Long a, Long b, Collection<InterestStatus> statuses) {
        return findActiveBetweenCandidates(a, b, statuses, PageRequest.of(0, 1)).stream().findFirst();
    }

    @Query("select i from Interest i where ((i.senderProfile.id = :a and i.receiverProfile.id = :b) or (i.senderProfile.id = :b and i.receiverProfile.id = :a)) and i.status in :statuses order by i.sentAt desc, i.id desc")
    List<Interest> findActiveBetweenCandidates(@Param("a") Long a, @Param("b") Long b, @Param("statuses") Collection<InterestStatus> statuses, Pageable pageable);

    boolean existsBySenderProfileIdAndSentAtBetween(Long senderProfileId, LocalDateTime from, LocalDateTime to);

    long countBySenderProfileIdAndSentAtBetween(Long senderProfileId, LocalDateTime from, LocalDateTime to);

    long countBySentAtBetween(LocalDateTime from, LocalDateTime to);

    Page<Interest> findBySenderProfileId(Long senderProfileId, Pageable pageable);

    Page<Interest> findByReceiverProfileId(Long receiverProfileId, Pageable pageable);

    Page<Interest> findBySenderProfileIdOrReceiverProfileIdAndStatus(Long senderProfileId, Long receiverProfileId, InterestStatus status, Pageable pageable);

    @Query("select i from Interest i where i.status = :status and (i.senderProfile.id = :profileId or i.receiverProfile.id = :profileId)")
    Page<Interest> findForProfileByStatus(@Param("profileId") Long profileId, @Param("status") InterestStatus status, Pageable pageable);
}
