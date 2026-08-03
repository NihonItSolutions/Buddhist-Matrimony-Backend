package com.matrimony.backend.repository;

import com.matrimony.backend.entity.SupportTicket;
import com.matrimony.backend.enums.SupportTicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Page<SupportTicket> findByUserId(Long userId, Pageable pageable);

    long countByStatus(SupportTicketStatus status);
}
