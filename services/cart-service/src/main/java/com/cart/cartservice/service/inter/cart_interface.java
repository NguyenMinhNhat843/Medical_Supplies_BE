package com.cart.cartservice.service.inter;

import com.cart.cartservice.dto.CartWithItems;
import com.cart.cartservice.dto.CartWithItemsDTO;
import com.cart.cartservice.entity.Cart;

import java.util.List;


public interface cart_interface {
    Cart createCart(Long userId);
    Cart getCartById(Long id);
    List<Cart> getAllCarts();
    Cart updateCart(Long id, Long newUserId);
    void deleteCart(Long id);


    Cart getCartByUserId(Long userId);
    CartWithItems addToCart(Long userId, Long productId, int quantity);
    CartWithItemsDTO getCartWithProductDetails(Long userId);
}
