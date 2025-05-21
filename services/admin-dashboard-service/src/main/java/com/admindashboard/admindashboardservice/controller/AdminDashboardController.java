package com.admindashboard.admindashboardservice.controller;

import com.admindashboard.admindashboardservice.dto.InventoryDetailDTO;
import com.admindashboard.admindashboardservice.dto.InventorySummaryDTO;
import com.admindashboard.admindashboardservice.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Autowired
    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    // API để lấy thông tin tổng quan kho
    @GetMapping("/api/dashboard/inventory-summary")
    public InventorySummaryDTO getInventorySummary() {
        return adminDashboardService.getInventorySummary();
    }
    
    // API để lấy danh sách sản phẩm có số lượng thấp
    @GetMapping("/api/dashboard/low-stock")
    public List<InventoryDetailDTO> getLowStockProducts() {
        return adminDashboardService.getLowStockProducts();
    }
    
    // API để lấy chi tiết tồn kho
    @GetMapping("/api/dashboard/inventory-details")
    public List<InventoryDetailDTO> getInventoryDetails() {
        return adminDashboardService.getInventoryDetails();
    }

    // API để lấy thông tin tổng quan người dùng
    @GetMapping("/api/dashboard/user-summary")
    public Object getUserSummary() {
        return adminDashboardService.getUserSummary();
    }
}
