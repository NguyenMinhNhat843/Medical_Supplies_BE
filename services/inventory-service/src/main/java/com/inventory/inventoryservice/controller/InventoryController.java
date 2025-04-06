package com.inventory.inventoryservice.controller;

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

    // Tìm sản phẩm theo tên người dùng (username)
    @GetMapping("/username/{username}")
    public ResponseEntity<Inventory> getInventoryByUsername(@PathVariable("username") String username) {
        Optional<Inventory> inventory = inventoryService.getInventoryByUsername(username);
        return inventory.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
