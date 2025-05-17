package com.cart.cartservice.Client;

import com.cart.cartservice.dto.CartItemDTO;
import com.cart.cartservice.dto.CartItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class CartItemClient {

    private final String BASE_URL = "http://localhost:8089/api/cart-items"; // thay bằng URL của cartItem service

    @Autowired
    private RestTemplate restTemplate;

    public void createCartItem(CartItemRequest request) {
        restTemplate.postForObject(BASE_URL, request, Void.class);
    }

    public List<CartItemDTO> getItemsByCartId(Long cartId) {
        String url = BASE_URL + "/cart/" + cartId;
        CartItemDTO[] response = restTemplate.getForObject(url, CartItemDTO[].class);
        return Arrays.asList(response);
    }
}

