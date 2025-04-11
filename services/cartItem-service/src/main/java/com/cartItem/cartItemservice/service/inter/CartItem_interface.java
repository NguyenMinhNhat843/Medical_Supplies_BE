package com.cartItem.cartItemservice.service.inter;

import com.cartItem.cartItemservice.dto.CartItemRequest;
import com.cartItem.cartItemservice.entity.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItem_interface {
    void addCartItem(CartItemRequest request);
    List<CartItem> getItemsByCartId(Long cartId);
}

