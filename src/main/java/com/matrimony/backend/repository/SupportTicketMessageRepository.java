package com.matrimony.backend.repository;

import com.matrimony.backend.entity.SupportTicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketMessageRepository extends JpaRepository<SupportTicketMessage, Long> {
}
