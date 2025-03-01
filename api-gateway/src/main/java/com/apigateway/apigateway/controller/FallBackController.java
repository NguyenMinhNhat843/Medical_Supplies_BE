package com.apigateway.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallBackController {
    @GetMapping("/auth")
    public String authFallback() {
        return "Auth Service hiện không khả dụng. Vui lòng thử lại sau!";
    }

    @GetMapping("/products")
    public String productFallback() {
        return "Product Service hiện không khả dụng. Vui lòng thử lại sau!";
    }

    @GetMapping("/orders")
    public String orderFallback() {
        return "Order Service hiện không khả dụng. Vui lòng thử lại sau!";
    }
}
