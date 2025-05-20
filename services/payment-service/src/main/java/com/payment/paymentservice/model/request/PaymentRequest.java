package com.payment.paymentservice.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
}
