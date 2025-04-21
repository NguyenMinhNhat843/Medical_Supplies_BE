package com.cart.cartservice.Client;

import com.cart.cartservice.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

    private final String BASE_URL = "http://localhost:8085/api/products"; // Giả sử product-service chạy ở 8083

    @Autowired
    private RestTemplate restTemplate;

    public ProductDTO getProductById(Long productId) {
        return restTemplate.getForObject(BASE_URL + "/" + productId, ProductDTO.class);
    }
}

