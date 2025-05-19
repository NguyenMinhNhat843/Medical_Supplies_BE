package com.payment.paymentservice.services;

import com.payment.paymentservice.entity.PaymentEntity;
import com.payment.paymentservice.model.dto.PaymentDTO;
import com.payment.paymentservice.model.request.PaymentRequest;

import java.util.List;

public interface PaymentService {
    PaymentDTO createPayment(PaymentRequest request);
    PaymentDTO updatePaymentStatus(Long id, String status);

    List<PaymentDTO> getPaymentsByOrderId(Long orderId);

    void confirmBankTransfer(Long orderId);
}
