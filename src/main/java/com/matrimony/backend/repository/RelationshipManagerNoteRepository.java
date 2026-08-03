package com.matrimony.backend.repository;

import com.matrimony.backend.entity.RelationshipManagerNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationshipManagerNoteRepository extends JpaRepository<RelationshipManagerNote, Long> {
    List<RelationshipManagerNote> findByManagerIdAndCustomerIdOrderByCreatedAtDesc(Long managerId, Long customerId);
}
