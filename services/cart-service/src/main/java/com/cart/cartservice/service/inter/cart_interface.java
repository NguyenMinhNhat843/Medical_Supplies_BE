package com.cart.cartservice.service.inter;

import com.cart.cartservice.entity.Cart;


public interface cart_interface {
    Cart getCartByUserId(Long userId);
    Cart createCart(Long userId);
}
