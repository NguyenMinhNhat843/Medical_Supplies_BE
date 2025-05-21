package com.cart.cartservice.service.impl;

import com.cart.cartservice.Client.CartItemClient;
import com.cart.cartservice.Client.ProductClient;
import com.cart.cartservice.dto.*;
import com.cart.cartservice.entity.Cart;
import com.cart.cartservice.repository.CartRedisRepository;
import com.cart.cartservice.repository.CartRepository;
import com.cart.cartservice.service.inter.cart_interface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService implements cart_interface {

    // Thêm Logger
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartRedisRepository cartRedisRepository; // Thêm repository cho Redis

    @Autowired
    private CartItemClient cartItemClient;

    @Autowired
    private ProductClient productClient;

    @Override
    public Cart getCartByUserId(Long userId) {
        long start = System.nanoTime();
        logger.info("Attempting to get cart for userId: {}", userId);
        Cart cart = cartRedisRepository.findByUserId(userId);
        if (cart != null) {
            logger.info("Cart found in Redis for userId: {}", userId);
        } else {
            logger.info("Cart not found in Redis for userId: {}, checking database", userId);
            cart = cartRepository.findByUserId(userId).orElse(null);
            if (cart != null) {
                logger.info("Cart found in database for userId: {}, saving to Redis", userId);
                cartRedisRepository.save(cart);
            } else {
                logger.info("No cart found for userId: {}", userId);
            }
        }
        long end = System.nanoTime();
        logger.info("getCartByUserId for userId: {} completed in {} ms", userId, (end - start) / 1_000_000.0);
        return cart;
    }

    @Override
    public Cart createCart(Long userId) {
        long start = System.nanoTime();
        logger.info("Creating new cart for userId: {}", userId);
        Cart cart = Cart.builder().userId(userId).build();
        cart = cartRepository.save(cart);
        logger.info("Cart saved to database for userId: {} with id: {}", userId, cart.getId());
        cartRedisRepository.save(cart);
        logger.info("Cart saved to Redis for userId: {}", userId);
        long end = System.nanoTime();
        logger.info("createCart for userId: {} completed in {} ms", userId, (end - start) / 1_000_000.0);
        return cart;
    }

    @Override
    public Cart getCartById(Long id) {
        // 1. Lấy từ database
        Cart cart = cartRepository.findById(id).orElse(null);

        // 2. Nếu tìm thấy, lưu vào Redis với key là userId
        if (cart != null) {
            cartRedisRepository.save(cart);
        }
        return cart;
    }

    @Override
    public List<Cart> getAllCarts() {
        // Lấy từ database (Redis không phù hợp cho thao tác lấy tất cả)
        return cartRepository.findAll();
    }

    @Override
    public Cart updateCart(Long id, Long newUserId) {
        // 1. Lấy từ database
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2. Cập nhật userId
        cart.setUserId(newUserId);

        // 3. Lưu vào database
        cart = cartRepository.save(cart);

        // 4. Cập nhật Redis (xóa key cũ và lưu key mới)
        cartRedisRepository.deleteByUserId(cart.getUserId()); // Xóa key cũ
        cartRedisRepository.save(cart); // Lưu key mới

        return cart;
    }

    @Override
    public void deleteCart(Long id) {
        // 1. Lấy cart từ database để lấy userId
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2. Xóa khỏi database
        cartRepository.deleteById(id);

        // 3. Xóa khỏi Redis
        cartRedisRepository.deleteByUserId(cart.getUserId());
    }

    @Override
    public CartWithItems addToCart(Long userId, Long productId, int quantity) {
        long start = System.nanoTime();
        logger.info("Adding to cart for userId: {}, productId: {}, quantity: {}", userId, productId, quantity);

        Cart cart = cartRedisRepository.findByUserId(userId);
        if (cart == null) {
            logger.info("Cart not found in Redis for userId: {}, checking database", userId);
            cart = cartRepository.findByUserId(userId).orElseGet(() -> {
                logger.info("Cart not found in database, creating new cart for userId: {}", userId);
                return cartRepository.save(new Cart(userId));
            });
            cartRedisRepository.save(cart);
            logger.info("Cart saved to Redis for userId: {}", userId);
        } else {
            logger.info("Cart found in Redis for userId: {}", userId);
        }

        List<CartItemDTO> existingItems = cartItemClient.getItemsByCartId(cart.getId());
        logger.info("Fetched {} cart items for cartId: {}", existingItems.size(), cart.getId());

        Optional<CartItemDTO> existingItem = existingItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemDTO item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            CartItemRequest updateRequest = new CartItemRequest(cart.getId(), productId, newQuantity);
            cartItemClient.updateCartItem(item.getId(), updateRequest);
            logger.info("Updated cart item for productId: {} with new quantity: {}", productId, newQuantity);
        } else {
            CartItemRequest request = new CartItemRequest(cart.getId(), productId, quantity);
            cartItemClient.createCartItem(request);
            logger.info("Created new cart item for productId: {}", productId);
        }

        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());
        logger.info("Fetched updated {} cart items for cartId: {}", items.size(), cart.getId());

        long end = System.nanoTime();
        logger.info("addToCart for userId: {} completed in {} ms", userId, (end - start) / 1_000_000.0);
        return new CartWithItems(cart, items);
    }

    @Override
    public CartWithItemsDTO getCartWithProductDetails(Long userId) {
        // 1. Lấy cart từ Redis
        Cart cart = cartRedisRepository.findByUserId(userId);
        if (cart == null) {
            cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));
            cartRedisRepository.save(cart); // Lưu vào Redis
        }

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

    @Override
    public CartWithItems deleteCartItem(Long userId, Long cartItemId) {
        // 1. Tìm giỏ hàng của người dùng
        Cart cart = cartRedisRepository.findByUserId(userId);
        if (cart == null) {
            cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
            cartRedisRepository.save(cart);
        }

        // 2. Xóa CartItem thông qua CartItemClient
        cartItemClient.deleteCartItem(cartItemId);

        // 3. Lấy lại danh sách item từ cart-item-service
        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());

        return new CartWithItems(cart, items);
    }

    @Override
    public CartWithItems incrementCartItemQuantity(Long userId, Long cartItemId, int amount) {
        Cart cart = cartRedisRepository.findByUserId(userId);
        if (cart == null) {
            cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
            cartRedisRepository.save(cart);
        }
        try {
            cartItemClient.incrementCartItemQuantity(cartItemId, amount);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("CartItem not found: " + cartItemId);
            }
            throw e;
        }
        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());
        return new CartWithItems(cart, items);
    }

    @Override
    public CartWithItems decrementCartItemQuantity(Long userId, Long cartItemId, int amount) {
        Cart cart = cartRedisRepository.findByUserId(userId);
        if (cart == null) {
            cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
            cartRedisRepository.save(cart);
        }
        try {
            cartItemClient.decrementCartItemQuantity(cartItemId, amount);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("CartItem not found: " + cartItemId);
            }
            throw e;
        }
        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());
        return new CartWithItems(cart, items);
    }
}