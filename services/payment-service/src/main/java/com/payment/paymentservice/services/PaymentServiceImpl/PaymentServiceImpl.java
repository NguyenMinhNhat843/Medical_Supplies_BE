package com.payment.paymentservice.services.PaymentServiceImpl;

import com.payment.paymentservice.converter.PaymentConverter;
import com.payment.paymentservice.entity.PaymentEntity;
import com.payment.paymentservice.model.dto.PaymentDTO;
import com.payment.paymentservice.model.request.PaymentRequest;
import com.payment.paymentservice.model.request.PaymentUpdateRequest;
import com.payment.paymentservice.repository.PaymentRepository;
import com.payment.paymentservice.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentConverter paymentConverter;

    @Value("${order-service.url}") // ví dụ: http://localhost:8082
    private String orderServiceUrl;

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public PaymentDTO createPayment(PaymentRequest request) {
        if (paymentRepository.existsByOrderId(Math.toIntExact(request.getOrderId()))) {
            throw new IllegalStateException("Đơn hàng này đã có thanh toán rồi.");
        }

        PaymentEntity payment = paymentConverter.toEntity(request);
        payment.setStatus("PENDING"); // trạng thái thanh toán ban đầu

        //  Gán transactionId nếu là COD
        if ("COD".equalsIgnoreCase(payment.getPaymentMethod())) {
            String txId = "COD-" + request.getOrderId() + "-" + System.currentTimeMillis();
            payment.setTransactionId(txId);
        }

        payment = paymentRepository.save(payment);

        //  Gọi order-service cập nhật thông tin đơn hàng
        try {
            String url = orderServiceUrl + "/api/orders/" + payment.getOrderId() + "/payment-info";
            PaymentUpdateRequest update = new PaymentUpdateRequest();

            update.setPaymentMethod(request.getPaymentMethod()); // lấy từ FE
            update.setStatus("PENDING");

            // Nếu COD thì UNPAID, còn lại (MOMO, ZaloPay...) là PAID (nếu đã thanh toán thành công)
            if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
                update.setPaymentStatus("UNPAID");
            } else {
                update.setPaymentStatus("PAID");
            }

            restTemplate.put(url, update);
        } catch (Exception e) {
            e.printStackTrace(); // hoặc log.warning(...)
        }

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
