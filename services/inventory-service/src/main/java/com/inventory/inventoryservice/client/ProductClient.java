package com.inventory.inventoryservice.client;

import com.inventory.inventoryservice.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {
    private final String BASE_URL = "http://product-service/api/products";
    
    @Autowired
    private RestTemplate restTemplate;
    
    public ProductDTO getProductById(Long productId) {
        return restTemplate.getForObject(BASE_URL + "/" + productId, ProductDTO.class);
    }
} 