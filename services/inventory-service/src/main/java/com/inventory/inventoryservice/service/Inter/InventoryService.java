package com.inventory.inventoryservice.service.Inter;

import com.inventory.inventoryservice.dto.InventoryDetailDTO;
import com.inventory.inventoryservice.entity.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    List<Inventory> getAllProducts(); // Lấy tất cả sản phẩm
    Optional<Inventory> getProductById(Long id); // Lấy sản phẩm theo ID
    Inventory saveProduct(Inventory inventory); // Lưu sản phẩm vào cơ sở dữ liệu
    void deleteProduct(Long id); // Xóa sản phẩm theo ID
    Optional<Inventory> getInventoryByProductId(Long productId); // Tìm sản phẩm theo product_id
    
    // Phương thức mới để lấy thông tin chi tiết inventory cùng với thông tin sản phẩm
    InventoryDetailDTO getInventoryWithProductDetails(Long id);
    List<InventoryDetailDTO> getAllInventoriesWithProductDetails();
    
    // Quản lý trạng thái tồn kho
    boolean updateQuantity(Long id, int changeAmount);
    boolean hasAvailableStock(Long productId, int requestedQuantity);
    
    // Báo cáo tồn kho
    List<InventoryDetailDTO> getLowStockProducts(int threshold);
}
