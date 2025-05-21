package com.cart.cartservice.repository;

import com.cart.cartservice.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRedisRepository {

    private static final String CART_KEY_PREFIX = "cart:user:";

    private final RedisTemplate<String, Object> redisTemplate;

    // Create/Update: Lưu giỏ hàng vào Redis
    public void save(Cart cart) {
        String key = CART_KEY_PREFIX + cart.getUserId();
        redisTemplate.opsForValue().set(key, cart);
    }

    // Read: Lấy giỏ hàng từ Redis
    public Cart findByUserId(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        return (Cart) redisTemplate.opsForValue().get(key);
    }

    // Delete: Xóa giỏ hàng khỏi Redis
    public void deleteByUserId(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    // Kiểm tra giỏ hàng có tồn tại không
    public boolean existsByUserId(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        return redisTemplate.hasKey(key);
    }
}