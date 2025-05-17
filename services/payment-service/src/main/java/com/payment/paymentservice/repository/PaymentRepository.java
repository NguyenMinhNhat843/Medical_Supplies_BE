package com.payment.paymentservice.repository;

import com.payment.paymentservice.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Integer> {
    List<PaymentEntity> findByOrderId(Long orderId);
    boolean existsByOrderId(Integer orderId);

}
