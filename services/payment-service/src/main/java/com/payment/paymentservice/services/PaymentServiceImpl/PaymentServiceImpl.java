package com.payment.paymentservice.services.PaymentServiceImpl;

import com.payment.paymentservice.converter.PaymentConverter;
import com.payment.paymentservice.entity.PaymentEntity;
import com.payment.paymentservice.model.dto.PaymentDTO;
import com.payment.paymentservice.model.request.PaymentRequest;
import com.payment.paymentservice.repository.PaymentRepository;
import com.payment.paymentservice.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentConverter paymentConverter;
    @Override
    public PaymentDTO createPayment(PaymentRequest request) {
        PaymentEntity payment = paymentConverter.toEntity(request);
        payment.setStatus("PENDING"); // default
        payment = paymentRepository.save(payment);
        return paymentConverter.toDTO(payment);
    }

    @Override
    public PaymentDTO updatePaymentStatus(Long id, String status) {
        PaymentEntity payment = paymentRepository.findById(Math.toIntExact(id)).orElse(null);
        if (payment == null) return null;
        payment.setStatus(status);
        return paymentConverter.toDTO(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
        return paymentConverter.toDTOList(paymentRepository.findByOrderId(orderId));
    }
}
