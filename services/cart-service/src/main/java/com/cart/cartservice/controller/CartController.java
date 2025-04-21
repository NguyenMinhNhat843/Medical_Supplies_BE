package com.cart.cartservice.controller;

import com.cart.cartservice.Client.CartItemClient;
import com.cart.cartservice.dto.AddToCartRequest;
import com.cart.cartservice.dto.CartItemDTO;
import com.cart.cartservice.dto.CartWithItems;
import com.cart.cartservice.dto.CartWithItemsDTO;
import com.cart.cartservice.entity.Cart;
import com.cart.cartservice.service.inter.cart_interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private cart_interface cartService;

    @Autowired
    private CartItemClient cartItemClient;

    // Create
    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.createCart(userId));
    }

    // Read All
    @GetMapping
    public ResponseEntity<List<Cart>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    // Read One
    @GetMapping("/by-id/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Cart> updateCart(@PathVariable Long id, @RequestParam Long newUserId) {
        return ResponseEntity.ok(cartService.updateCart(id, newUserId));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    // Trả về Cart đơn giản với cartItemDTO (không có thông tin sản phẩm)
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartSimple(@PathVariable Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) return ResponseEntity.notFound().build();

        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("cart", cart);
        response.put("items", items);

        return ResponseEntity.ok(response);
    }

    // Trả về Cart chi tiết với thông tin sản phẩm
    @GetMapping("/{userId}/details")
    public ResponseEntity<CartWithItemsDTO> getCart(@PathVariable Long userId) {
        CartWithItemsDTO cart = cartService.getCartWithProductDetails(userId);
        return ResponseEntity.ok(cart);
    }

    // Thêm sản phẩm vào cart và trả về chi tiết cart
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartWithItemsDTO> addToCart(
            @PathVariable Long userId,
            @RequestBody AddToCartRequest request) {

        // Thêm vào cart (tạo Cart + gọi cartItem-service để thêm sản phẩm)
        cartService.addToCart(userId, request.getProductId(), request.getQuantity());

        // Gọi lại hàm lấy chi tiết cart để trả về luôn thông tin đầy đủ sản phẩm
        CartWithItemsDTO detailedCart = cartService.getCartWithProductDetails(userId);
        return ResponseEntity.ok(detailedCart);
    }

}

