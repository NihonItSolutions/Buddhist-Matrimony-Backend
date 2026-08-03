package com.matrimony.backend.repository;

import com.matrimony.backend.entity.ContactRequest;
import com.matrimony.backend.enums.ContactRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {
    Optional<ContactRequest> findFirstByRequesterProfileIdAndReceiverProfileIdOrderByRequestedAtDesc(Long requesterId, Long receiverId);

    boolean existsByRequesterProfileIdAndReceiverProfileIdAndStatus(Long requesterId, Long receiverId, ContactRequestStatus status);

    Page<ContactRequest> findByRequesterProfileId(Long requesterId, Pageable pageable);

    Page<ContactRequest> findByReceiverProfileId(Long receiverId, Pageable pageable);
}
