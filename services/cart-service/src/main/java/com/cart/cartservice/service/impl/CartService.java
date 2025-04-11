package com.cart.cartservice.service.impl;

import com.cart.cartservice.Client.CartItemClient;
import com.cart.cartservice.dto.CartItemDTO;
import com.cart.cartservice.dto.CartItemRequest;
import com.cart.cartservice.dto.CartWithItems;
import com.cart.cartservice.entity.Cart;
import com.cart.cartservice.repository.CartRepository;
import com.cart.cartservice.service.inter.cart_interface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService implements cart_interface {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private  CartItemClient cartItemClient;

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Cart createCart(Long userId) {
        Cart cart = Cart.builder().userId(userId).build();
        return cartRepository.save(cart);
    }

    @Override
    public CartWithItems addToCart(Long userId, Long productId, int quantity) {
        // 1. Tìm hoặc tạo cart mới
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        // 2. Gửi request tạo cartItem
        CartItemRequest request = new CartItemRequest(cart.getId(), productId, quantity);
        cartItemClient.createCartItem(request);

        // 3. Lấy lại danh sách item từ cart-item-service
        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());

        return new CartWithItems(cart, items);
    }
}


