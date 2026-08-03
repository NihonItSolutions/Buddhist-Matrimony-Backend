package com.matrimony.backend.repository;

import com.matrimony.backend.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("select c from Conversation c where (c.profileOne.id = :a and c.profileTwo.id = :b) or (c.profileOne.id = :b and c.profileTwo.id = :a)")
    Optional<Conversation> findBetween(@Param("a") Long a, @Param("b") Long b);

    @Query("select c from Conversation c where c.profileOne.id = :profileId or c.profileTwo.id = :profileId")
    Page<Conversation> findForProfile(@Param("profileId") Long profileId, Pageable pageable);
}
