package com.inventory.inventoryservice.repository;

import com.inventory.inventoryservice.entity.Inventory; // Sửa lại import cho đúng
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
//    Optional<Inventory> findByUsername(String username); // Phương thức tìm kiếm theo tên người dùng (hoặc tài khoản)
}
