package com.payment.paymentservice.controller;

import com.payment.paymentservice.model.dto.PaymentDTO;
import com.payment.paymentservice.model.request.PaymentRequest;
import com.payment.paymentservice.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentRequest request) {
        PaymentDTO paymentDTO = paymentService.createPayment(request);
        return ResponseEntity.ok(paymentDTO);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PaymentDTO updated = paymentService.updatePaymentStatus(id, status);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getByOrder(@PathVariable Integer orderId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByOrderId(Long.valueOf(orderId));
        return ResponseEntity.ok(payments);
    }
}
