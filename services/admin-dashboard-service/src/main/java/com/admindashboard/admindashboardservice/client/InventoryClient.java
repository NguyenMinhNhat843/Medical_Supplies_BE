package com.admindashboard.admindashboardservice.client;

import com.admindashboard.admindashboardservice.dto.InventoryDetailDTO;
import com.admindashboard.admindashboardservice.dto.InventorySummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    
    @GetMapping("/api/inventory/summary")
    InventorySummaryDTO getInventorySummary();
    
    @GetMapping("/api/inventory/details")
    List<InventoryDetailDTO> getInventoryDetails();
    
    @GetMapping("/api/inventory/low-stock")
    List<InventoryDetailDTO> getLowStockProducts();
}
