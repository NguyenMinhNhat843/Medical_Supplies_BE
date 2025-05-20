package com.cartItem.cartItemservice.service.inter;

import com.cartItem.cartItemservice.dto.CartItemRequest;
import com.cartItem.cartItemservice.entity.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItem_interface {
    void addCartItem(CartItemRequest request);
    List<CartItem> getItemsByCartId(Long cartId);
    Optional<CartItem> updateCartItem(Long cartItemId, CartItemRequest request);
    boolean deleteCartItem(Long cartItemId);
    Optional<CartItem> incrementQuantity(Long cartItemId, int amount);
    Optional<CartItem> decrementQuantity(Long cartItemId, int amount);
}

