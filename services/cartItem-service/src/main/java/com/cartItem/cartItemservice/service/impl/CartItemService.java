package com.cartItem.cartItemservice.service.impl;

import com.cartItem.cartItemservice.dto.CartItemRequest;
import com.cartItem.cartItemservice.entity.CartItem;
import com.cartItem.cartItemservice.repository.CartItemRepository;
import com.cartItem.cartItemservice.service.inter.CartItem_interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CartItemService implements CartItem_interface {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public void addCartItem(CartItemRequest request) {
        CartItem item = new CartItem();
        item.setCartId(request.getCartId());
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
    }

    @Override
    public List<CartItem> getItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Override
    public Optional<CartItem> updateCartItem(Long cartItemId, CartItemRequest request) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            cartItem.setQuantity(request.getQuantity());
            // Cập nhật các trường khác nếu cần, ví dụ: cartId, productId
            cartItemRepository.save(cartItem);
            return Optional.of(cartItem);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteCartItem(Long cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CartItem> incrementQuantity(Long cartItemId, int amount) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            int newQuantity = cartItem.getQuantity() + amount;
            if (newQuantity <= 0) {
                cartItemRepository.deleteById(cartItemId);
                return Optional.empty();
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
            return Optional.of(cartItem);
        }
        return Optional.empty();
    }

    @Override
    public Optional<CartItem> decrementQuantity(Long cartItemId, int amount) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            int newQuantity = cartItem.getQuantity() - amount;
            if (newQuantity <= 0) {
                cartItemRepository.deleteById(cartItemId);
                return Optional.empty();
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
            return Optional.of(cartItem);
        }
        return Optional.empty();
    }
}

