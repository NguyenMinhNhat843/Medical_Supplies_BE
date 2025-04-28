package com.inventory.inventoryservice.service.Implements;

import com.inventory.inventoryservice.entity.Inventory;
import com.inventory.inventoryservice.repository.InventoryRepository;
import com.inventory.inventoryservice.service.Inter.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Đánh dấu lớp này là Service trong Spring
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Inventory> getAllProducts() {
        return inventoryRepository.findAll(); // Trả về tất cả sản phẩm
    }

    @Override
    public Optional<Inventory> getProductById(Long id) {
        return inventoryRepository.findById(id); // Trả về sản phẩm theo ID
    }

    @Override
    public Inventory saveProduct(Inventory inventory) {
        return inventoryRepository.save(inventory); // Lưu sản phẩm vào cơ sở dữ liệu
    }

    @Override
    public void deleteProduct(Long id) {
        inventoryRepository.deleteById(id); // Xóa sản phẩm theo ID
    }

//    @Override
//    public Optional<Inventory> getInventoryByUsername(String username) {
//        return inventoryRepository.findByUsername(username); // Tìm sản phẩm theo username
//    }
}
