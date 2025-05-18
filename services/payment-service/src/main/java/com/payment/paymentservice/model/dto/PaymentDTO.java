package com.payment.paymentservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentDTO {
    private Integer paymentId;
    private Integer orderId;
    private String paymentMethod;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private String transferContent;
    private String qrCodeBase64;
    private String qrRawContent;        // Mã QR dạng raw string để scan trực tiếp

}