package com.cartItem.cartItemservice.controller;

import com.cartItem.cartItemservice.dto.CartItemRequest;
import com.cartItem.cartItemservice.entity.CartItem;
import com.cartItem.cartItemservice.service.impl.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<Void> addCartItem(@RequestBody CartItemRequest request) {
        cartItemService.addCartItem(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItem> getItemsByCartId(@PathVariable Long cartId) {
        return cartItemService.getItemsByCartId(cartId);
    }
}
