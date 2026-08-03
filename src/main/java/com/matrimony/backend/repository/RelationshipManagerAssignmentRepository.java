package com.matrimony.backend.repository;

import com.matrimony.backend.entity.RelationshipManagerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationshipManagerAssignmentRepository extends JpaRepository<RelationshipManagerAssignment, Long> {
    List<RelationshipManagerAssignment> findByManagerId(Long managerId);

    boolean existsByManagerIdAndCustomerId(Long managerId, Long customerId);

    void deleteByManagerIdAndCustomerId(Long managerId, Long customerId);
}
