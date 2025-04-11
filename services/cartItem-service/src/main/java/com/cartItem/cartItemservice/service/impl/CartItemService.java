package com.cartItem.cartItemservice.service.impl;

import com.cartItem.cartItemservice.entity.CartItem;
import com.cartItem.cartItemservice.repository.CartItemRepository;
import com.cartItem.cartItemservice.service.inter.CartItem_interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartItemService implements CartItem_interface {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> getItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
}

