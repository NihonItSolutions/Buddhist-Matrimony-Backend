package com.matrimony.backend.repository;

import com.matrimony.backend.entity.User;
import com.matrimony.backend.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByMobileNumber(String mobileNumber);

    Optional<User> findByMatrimonyId(String matrimonyId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobileNumber(String mobileNumber);

    @Query("select coalesce(max(u.id), 0) from User u")
    Long maxId();

    long countByAccountStatus(AccountStatus status);

    Page<User> findByDeletedAtIsNull(Pageable pageable);
}
