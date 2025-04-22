package com.inventory.inventoryservice.service.Implements;

import com.inventory.inventoryservice.client.ProductClient;
import com.inventory.inventoryservice.dto.InventoryDetailDTO;
import com.inventory.inventoryservice.dto.ProductDTO;
import com.inventory.inventoryservice.entity.Inventory;
import com.inventory.inventoryservice.repository.InventoryRepository;
import com.inventory.inventoryservice.service.Inter.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Đánh dấu lớp này là Service trong Spring
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductClient productClient) {
        this.inventoryRepository = inventoryRepository;
        this.productClient = productClient;
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

    @Override
    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId); // Tìm sản phẩm theo productId
    }
    
    @Override
    public InventoryDetailDTO getInventoryWithProductDetails(Long id) {
        Optional<Inventory> optionalInventory = inventoryRepository.findById(id);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            InventoryDetailDTO detailDTO = new InventoryDetailDTO();
            detailDTO.setId(inventory.getId());
            detailDTO.setProductId(inventory.getProductId());
            detailDTO.setQuantity(inventory.getQuantity());
            detailDTO.setLastUpdate(inventory.getLastUpdate());
            
            try {
                ProductDTO product = productClient.getProductById(inventory.getProductId());
                detailDTO.setProduct(product);
            } catch (Exception e) {
                // Xử lý khi không thể kết nối đến product-service
                System.err.println("Error fetching product details: " + e.getMessage());
            }
            
            return detailDTO;
        }
        return null;
    }
    
    @Override
    public List<InventoryDetailDTO> getAllInventoriesWithProductDetails() {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<InventoryDetailDTO> detailDTOs = new ArrayList<>();
        
        for (Inventory inventory : inventories) {
            InventoryDetailDTO detailDTO = new InventoryDetailDTO();
            detailDTO.setId(inventory.getId());
            detailDTO.setProductId(inventory.getProductId());
            detailDTO.setQuantity(inventory.getQuantity());
            detailDTO.setLastUpdate(inventory.getLastUpdate());
            
            try {
                ProductDTO product = productClient.getProductById(inventory.getProductId());
                detailDTO.setProduct(product);
            } catch (Exception e) {
                // Xử lý khi không thể kết nối đến product-service
                System.err.println("Error fetching product details: " + e.getMessage());
            }
            
            detailDTOs.add(detailDTO);
        }
        
        return detailDTOs;
    }
    
    @Override
    public boolean updateQuantity(Long id, int changeAmount) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(id);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            int newQuantity = inventory.getQuantity() + changeAmount;
            if (newQuantity >= 0) {
                inventory.setQuantity(newQuantity);
                inventoryRepository.save(inventory);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAvailableStock(Long productId, int requestedQuantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        return inventoryOpt.isPresent() && inventoryOpt.get().getQuantity() >= requestedQuantity;
    }
    
    @Override
    public List<InventoryDetailDTO> getLowStockProducts(int threshold) {
        List<Inventory> lowStockInventories = inventoryRepository.findByQuantityLessThanEqual(threshold);
        List<InventoryDetailDTO> result = new ArrayList<>();
        
        for (Inventory inventory : lowStockInventories) {
            InventoryDetailDTO detailDTO = new InventoryDetailDTO();
            detailDTO.setId(inventory.getId());
            detailDTO.setProductId(inventory.getProductId());
            detailDTO.setQuantity(inventory.getQuantity());
            detailDTO.setLastUpdate(inventory.getLastUpdate());
            
            try {
                ProductDTO product = productClient.getProductById(inventory.getProductId());
                detailDTO.setProduct(product);
            } catch (Exception e) {
                System.err.println("Error fetching product details: " + e.getMessage());
            }
            
            result.add(detailDTO);
        }
        
        return result;
    }
}
