package com.inventory.inventoryservice.service.Inter;

import com.inventory.inventoryservice.entity.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    List<Inventory> getAllProducts(); // Lấy tất cả sản phẩm
    Optional<Inventory> getProductById(Long id); // Lấy sản phẩm theo ID
    Inventory saveProduct(Inventory inventory); // Lưu sản phẩm vào cơ sở dữ liệu
    void deleteProduct(Long id); // Xóa sản phẩm theo ID
//    Optional<Inventory> getInventoryByUsername(String username); // Tìm sản phẩm theo username
}
