package com.cart.cartservice.controller;

import com.cart.cartservice.Client.CartItemClient;
import com.cart.cartservice.dto.CartItemDTO;
import com.cart.cartservice.entity.Cart;
import com.cart.cartservice.service.inter.cart_interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private cart_interface cartService;

    @Autowired
    private CartItemClient cartItemClient;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartWithItems(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) return ResponseEntity.notFound().build();

        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("cart", cart);
        response.put("items", items);

        return ResponseEntity.ok(response);
    }
}

