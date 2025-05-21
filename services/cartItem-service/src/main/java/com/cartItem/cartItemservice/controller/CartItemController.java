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

    @PutMapping("/{cartItemId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long cartItemId, @RequestBody CartItemRequest request) {
        Optional<CartItem> updatedItem = cartItemService.updateCartItem(cartItemId, request);
        if (updatedItem.isPresent()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        boolean deleted = cartItemService.deleteCartItem(cartItemId);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{cartItemId}/increment")
    public ResponseEntity<Void> incrementCartItemQuantity(@PathVariable Long cartItemId, @RequestParam(defaultValue = "1") int amount) {
        Optional<CartItem> updatedItem = cartItemService.incrementQuantity(cartItemId, amount);
        if (updatedItem.isPresent() || updatedItem.isEmpty()) {
            return ResponseEntity.ok().build(); // Trả về OK kể cả khi xóa (quantity <= 0)
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{cartItemId}/decrement")
    public ResponseEntity<Void> decrementCartItemQuantity(@PathVariable Long cartItemId, @RequestParam(defaultValue = "1") int amount) {
        Optional<CartItem> updatedItem = cartItemService.decrementQuantity(cartItemId, amount);
        if (updatedItem.isPresent() || updatedItem.isEmpty()) {
            return ResponseEntity.ok().build(); // Trả về OK kể cả khi xóa (quantity <= 0)
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

