package com.order.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code")
    private String code;

    @Column(name = "discount_type")
    private String discountType; // "PERCENTAGE" hoặc "FIXED"

    @Column(name = "discount_value")
    private double discountValue; // Giá trị giảm (%, hoặc số tiền)

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "max_usage")
    private int maxUsage;

    @Column(name = "used_count")
    private int usedCount;

    @Column(name = "is_active")
    private boolean isActive;
}
