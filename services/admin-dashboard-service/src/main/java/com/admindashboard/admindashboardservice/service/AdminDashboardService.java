package com.admindashboard.admindashboardservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AdminDashboardService {

    private final RestTemplate restTemplate;

    @Autowired
    public AdminDashboardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Lấy thông tin tổng quan về số lượng sản phẩm từ inventory-service
    public Object getInventorySummary() {
        String url = "http://inventory-service/api/inventory/summary"; // Endpoint của inventory-service
        return restTemplate.getForObject(url, Object.class); // Gọi API và trả về thông tin
    }

    // Lấy thông tin tổng quan về người dùng từ user-service
    public Object getUserSummary() {
        String url = "http://user-service/api/users/summary"; // Endpoint của user-service
        return restTemplate.getForObject(url, Object.class); // Gọi API và trả về thông tin
    }

}
