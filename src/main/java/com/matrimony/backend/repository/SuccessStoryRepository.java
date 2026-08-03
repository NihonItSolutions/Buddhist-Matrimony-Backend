package com.matrimony.backend.repository;

import com.matrimony.backend.entity.SuccessStory;
import com.matrimony.backend.enums.SuccessStoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuccessStoryRepository extends JpaRepository<SuccessStory, Long> {
    Page<SuccessStory> findByStatus(SuccessStoryStatus status, Pageable pageable);

    Page<SuccessStory> findBySubmittedById(Long userId, Pageable pageable);
}
