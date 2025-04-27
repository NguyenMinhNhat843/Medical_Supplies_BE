package com.inventory.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String name; // Tên sản phẩm

    private String description; // Mô tả sản phẩm

    private int quantity; // Số lượng sản phẩm trong kho

    private double price; // Giá của sản phẩm
}
