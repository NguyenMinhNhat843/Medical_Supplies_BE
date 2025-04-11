package com.cart.cartservice.service.impl;

import com.cart.cartservice.entity.Cart;
import com.cart.cartservice.repository.CartRepository;
import com.cart.cartservice.service.inter.cart_interface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService implements cart_interface {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Cart createCart(Long userId) {
        Cart cart = Cart.builder().userId(userId).build();
        return cartRepository.save(cart);
    }
}


