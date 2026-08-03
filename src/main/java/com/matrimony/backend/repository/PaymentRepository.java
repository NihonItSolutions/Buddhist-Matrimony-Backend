package com.matrimony.backend.repository;

import com.matrimony.backend.entity.Payment;
import com.matrimony.backend.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

    Page<Payment> findByUserId(Long userId, Pageable pageable);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.status = :status and p.paidAt between :from and :to")
    BigDecimal sumAmountByStatusAndPaidAtBetween(@Param("status") PaymentStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
