package com.payment.paymentservice.model.request;

import lombok.Data;

@Data
public class PaymentUpdateRequest {
    private String paymentMethod;
    private String paymentStatus;
    private String status;
}