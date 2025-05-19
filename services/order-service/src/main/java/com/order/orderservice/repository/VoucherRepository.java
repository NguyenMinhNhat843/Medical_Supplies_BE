package com.order.orderservice.repository;

import com.order.orderservice.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Optional<Voucher> findByCodeAndExpiryDateAfterAndIsActiveTrue(String code, LocalDateTime now);
}