package com.order.orderservice.models;

import lombok.Data;

@Data
public class PaymentUpdateRequest {
    private String paymentMethod;
    private String paymentStatus;
    private String status;
}
