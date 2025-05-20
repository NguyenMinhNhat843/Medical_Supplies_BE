package com.inventory.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory") // Tên bảng trong cơ sở dữ liệu
@Getter
@Setter
@NoArgsConstructor // Constructor không tham số
@AllArgsConstructor // Constructor có tất cả các tham số
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID
    private Long id; // Mã sản phẩm

    @Column(name = "product_id")
    private Long productId;

    private int quantity; // Số lượng sản phẩm trong kho

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        lastUpdate = LocalDateTime.now();
    }
}
