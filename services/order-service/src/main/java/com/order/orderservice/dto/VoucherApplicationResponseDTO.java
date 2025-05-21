package com.order.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherApplicationResponseDTO {
    private Integer orderId;
    private Double originalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private String voucherCode;
    private LocalDateTime appliedAt;
}