package com.order.orderservice.client;

import com.order.orderservice.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class CartClient {

    private final RestTemplate restTemplate;
    @Value("${cart.service.url:http://localhost:8088/api/carts}")
    private String cartServiceUrl;

    @Autowired
    public CartClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CartWithItemsDTO getCartWithDetails(HttpServletRequest request) {
        String url = cartServiceUrl + "/details";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getHeader("Authorization"));
        headers.set("X-UserId", request.getHeader("X-UserId"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CartWithItemsDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Log lỗi hoặc xử lý tùy chỉnh
            System.err.println("Error fetching cart: " + e.getMessage());
            return null;
        }
    }
}