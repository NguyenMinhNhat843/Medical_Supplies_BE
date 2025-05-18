package com.payment.paymentservice.controller;

import com.payment.paymentservice.model.dto.PaymentDTO;
import com.payment.paymentservice.model.request.PaymentRequest;
import com.payment.paymentservice.services.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;


    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
        try {
            PaymentDTO dto = paymentService.createPayment(request);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmBankTransfer(@PathVariable Long orderId) {
        try {
            paymentService.confirmBankTransfer(orderId);
            return ResponseEntity.ok("Đã xác nhận chuyển khoản thành công cho đơn hàng " + orderId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        PaymentDTO updated = paymentService.updatePaymentStatus(id, status);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
