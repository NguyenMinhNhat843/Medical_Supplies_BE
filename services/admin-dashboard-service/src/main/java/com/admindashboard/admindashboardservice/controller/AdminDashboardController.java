package com.admindashboard.admindashboardservice.controller;

import com.admindashboard.admindashboardservice.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Autowired
    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    // API để lấy thông tin tổng quan kho
    @GetMapping("/api/dashboard/inventory-summary")
    public Object getInventorySummary() {
        return adminDashboardService.getInventorySummary(); // Trả về thông tin từ inventory-service
    }

    // API để lấy thông tin tổng quan người dùng
    @GetMapping("/api/dashboard/user-summary")
    public Object getUserSummary() {
        return adminDashboardService.getUserSummary(); // Trả về thông tin từ user-service
    }

}
