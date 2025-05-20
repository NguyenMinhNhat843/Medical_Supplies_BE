package com.cart.cartservice.service.impl;

import com.cart.cartservice.Client.CartItemClient;
import com.cart.cartservice.Client.ProductClient;
import com.cart.cartservice.dto.*;
import com.cart.cartservice.entity.Cart;
import com.cart.cartservice.repository.CartRepository;
import com.cart.cartservice.service.inter.cart_interface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService implements cart_interface {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private  CartItemClient cartItemClient;

    @Autowired
    private ProductClient productClient;

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
    public Cart getCartById(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Cart updateCart(Long id, Long newUserId) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setUserId(newUserId);
        return cartRepository.save(cart);
    }

    @Override
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    @Override
    public CartWithItems addToCart(Long userId, Long productId, int quantity) {
        // 1. Tìm hoặc tạo giỏ hàng mới
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        // 2. Kiểm tra xem CartItem với productId đã tồn tại chưa
        List<CartItemDTO> existingItems = cartItemClient.getItemsByCartId(cart.getId());
        Optional<CartItemDTO> existingItem = existingItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Cập nhật số lượng của CartItem hiện có
            CartItemDTO item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            CartItemRequest updateRequest = new CartItemRequest(cart.getId(), productId, newQuantity);
            cartItemClient.updateCartItem(item.getId(), updateRequest); // Giả sử updateCartItem tồn tại
        } else {
            // Tạo CartItem mới
            CartItemRequest request = new CartItemRequest(cart.getId(), productId, quantity);
            cartItemClient.createCartItem(request);
        }

        // 3. Lấy lại danh sách item từ cart-item-service
        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());

        return new CartWithItems(cart, items);
    }

    @Override
    public CartWithItemsDTO getCartWithProductDetails(Long userId) {
        // 1. Lấy cart theo userId
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));

        // 2. Lấy danh sách cartItems từ cartItem-service
        List<CartItemDTO> cartItems = cartItemClient.getItemsByCartId(cart.getId());

        // 3. Tạo danh sách CartItemDetailDTO
        List<CartItemDetailDTO> detailedItems = cartItems.stream().map(item -> {
            ProductDTO product = productClient.getProductById(item.getProductId());

            CartItemDetailDTO detail = new CartItemDetailDTO();
            detail.setId(item.getId());
            detail.setCartId(item.getCartId());
            detail.setQuantity(item.getQuantity());
            detail.setProduct(product);

            return detail;
        }).collect(Collectors.toList());

        // 4. Trả về CartWithItemsDTO
        CartWithItemsDTO result = new CartWithItemsDTO();
        result.setId(cart.getId());
        result.setUserId(cart.getUserId());
        result.setItems(detailedItems);

        return result;
    }
}


