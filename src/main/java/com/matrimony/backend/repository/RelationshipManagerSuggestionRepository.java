package com.matrimony.backend.repository;

import com.matrimony.backend.entity.RelationshipManagerSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationshipManagerSuggestionRepository extends JpaRepository<RelationshipManagerSuggestion, Long> {
    List<RelationshipManagerSuggestion> findByManagerIdAndCustomerIdOrderByCreatedAtDesc(Long managerId, Long customerId);
}
