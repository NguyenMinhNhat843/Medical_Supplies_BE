package com.admindashboard.admindashboardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AdminDashboardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminDashboardServiceApplication.class, args);
    }
}
