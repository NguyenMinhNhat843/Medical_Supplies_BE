package com.admindashboard.admindashboardservice.service;

import com.admindashboard.admindashboardservice.client.InventoryClient;
import com.admindashboard.admindashboardservice.dto.InventoryDetailDTO;
import com.admindashboard.admindashboardservice.dto.InventorySummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class AdminDashboardService {

    private final RestTemplate restTemplate;
    private final InventoryClient inventoryClient;

    @Autowired
    public AdminDashboardService(RestTemplate restTemplate, InventoryClient inventoryClient) {
        this.restTemplate = restTemplate;
        this.inventoryClient = inventoryClient;
    }

    // Lấy thông tin tổng quan về kho từ inventory-service
    public InventorySummaryDTO getInventorySummary() {
        try {
            return inventoryClient.getInventorySummary();
        } catch (Exception e) {
            // Xử lý lỗi khi service không khả dụng
            return new InventorySummaryDTO(0, 0, 0, 0);
        }
    }

    // Lấy danh sách sản phẩm có số lượng thấp từ inventory-service
    public List<InventoryDetailDTO> getLowStockProducts() {
        try {
            return inventoryClient.getLowStockProducts();
        } catch (Exception e) {
            // Xử lý lỗi khi service không khả dụng
            return Collections.emptyList();
        }
    }

    // Lấy chi tiết tồn kho từ inventory-service
    public List<InventoryDetailDTO> getInventoryDetails() {
        try {
            return inventoryClient.getInventoryDetails();
        } catch (Exception e) {
            // Xử lý lỗi khi service không khả dụng
            return Collections.emptyList();
        }
    }

    // Lấy thông tin tổng quan về người dùng từ user-service
    public Object getUserSummary() {
        try {
            String url = "http://user-service/api/users/summary"; 
            return restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            // Xử lý lỗi khi service không khả dụng
            return Collections.emptyMap();
        }
    }
}
