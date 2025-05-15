package com.order.orderservice.client;

import com.order.orderservice.dto.CartWithItemsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CartClient {
    private final RestTemplate restTemplate;

    @Autowired
    public CartClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CartWithItemsDTO getCartWithDetails(Long userId) {
        String url = "http://localhost:8088/api/carts/" + userId + "/details";
        ResponseEntity<CartWithItemsDTO> response =
                restTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {});
        return response.getBody();
    }
}

