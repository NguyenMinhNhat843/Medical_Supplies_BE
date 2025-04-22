package com.inventory.inventoryservice.controller;

import com.inventory.inventoryservice.dto.InventoryDetailDTO;
import com.inventory.inventoryservice.entity.Inventory;
import com.inventory.inventoryservice.service.Inter.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Đánh dấu đây là một RESTful Controller
@RequestMapping("/api/inventory") // Cấu hình đường dẫn API
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Lấy tất cả sản phẩm
    @GetMapping("/")
    public ResponseEntity<List<Inventory>> getAllProducts() {
        List<Inventory> products = inventoryService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getProductById(@PathVariable("id") Long id) {
        Optional<Inventory> product = inventoryService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Lưu một sản phẩm mới
    @PostMapping("/")
    public ResponseEntity<Inventory> saveProduct(@RequestBody Inventory inventory) {
        Inventory savedProduct = inventoryService.saveProduct(inventory);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // Xóa sản phẩm theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        inventoryService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Trả về 204 No Content nếu xóa thành công
    }

    // Tìm sản phẩm theo product_id
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable("productId") Long productId) {
        Optional<Inventory> inventory = inventoryService.getInventoryByProductId(productId);
        return inventory.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // Lấy inventory kèm thông tin sản phẩm chi tiết theo ID
    @GetMapping("/detail/{id}")
    public ResponseEntity<InventoryDetailDTO> getInventoryWithProductDetails(@PathVariable("id") Long id) {
        InventoryDetailDTO detail = inventoryService.getInventoryWithProductDetails(id);
        if (detail != null) {
            return new ResponseEntity<>(detail, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Lấy tất cả inventory kèm thông tin sản phẩm chi tiết
    @GetMapping("/details")
    public ResponseEntity<List<InventoryDetailDTO>> getAllInventoriesWithProductDetails() {
        List<InventoryDetailDTO> details = inventoryService.getAllInventoriesWithProductDetails();
        return new ResponseEntity<>(details, HttpStatus.OK);
    }
    
    // Cập nhật số lượng tồn kho
    @PutMapping("/update-quantity/{id}")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Long id, 
            @RequestParam int changeAmount) {
        
        boolean updated = inventoryService.updateQuantity(id, changeAmount);
        if (updated) {
            return ResponseEntity.ok("Số lượng tồn kho đã được cập nhật");
        }
        return ResponseEntity.badRequest().body("Không thể cập nhật số lượng tồn kho");
    }
    
    // Kiểm tra tồn kho
    @GetMapping("/check-stock")
    public ResponseEntity<Boolean> checkStock(
            @RequestParam Long productId, 
            @RequestParam int quantity) {
        
        boolean available = inventoryService.hasAvailableStock(productId, quantity);
        return ResponseEntity.ok(available);
    }
    
    // Lấy danh sách sản phẩm có tồn kho thấp
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDetailDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        
        List<InventoryDetailDTO> lowStockProducts = inventoryService.getLowStockProducts(threshold);
        return ResponseEntity.ok(lowStockProducts);
    }
}
