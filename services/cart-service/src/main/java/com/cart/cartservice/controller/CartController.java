package com.cart.cartservice.controller;

import com.cart.cartservice.Client.CartItemClient;
import com.cart.cartservice.dto.AddToCartRequest;
import com.cart.cartservice.dto.CartItemDTO;
import com.cart.cartservice.dto.CartWithItems;
import com.cart.cartservice.dto.CartWithItemsDTO;
import com.cart.cartservice.entity.Cart;
import com.cart.cartservice.service.inter.cart_interface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // Hàm tiện ích để trích xuất userId từ header X-UserId
    private ResponseEntity<?> extractUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-UserId");
        if (userIdHeader == null) {
            return ResponseEntity.badRequest().body("Thiếu header X-UserId");
        }

        try {
            Long userId = Long.parseLong(userIdHeader);
            return ResponseEntity.ok(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Header X-UserId không hợp lệ");
        }
    }

    // Create
    @PostMapping
    public ResponseEntity<?> createCart(HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse; // Trả về lỗi nếu userId không hợp lệ
        }
        Long userId = (Long) userIdResponse.getBody();
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
    public ResponseEntity<?> updateCart(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse; // Trả về lỗi nếu userId không hợp lệ
        }
        Long newUserId = (Long) userIdResponse.getBody();
        return ResponseEntity.ok(cartService.updateCart(id, newUserId));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    // Trả về Cart đơn giản với cartItemDTO (không có thông tin sản phẩm)
    @GetMapping("/me")
    public ResponseEntity<?> getCartSimple(HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse; // Trả về lỗi nếu userId không hợp lệ
        }
        Long userId = (Long) userIdResponse.getBody();

        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy giỏ hàng");
        }

        List<CartItemDTO> items = cartItemClient.getItemsByCartId(cart.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("cart", cart);
        response.put("items", items);

        return ResponseEntity.ok(response);
    }

    // Trả về Cart chi tiết với thông tin sản phẩm
    @GetMapping("/details")
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse; // Trả về lỗi nếu userId không hợp lệ
        }
        Long userId = (Long) userIdResponse.getBody();

        CartWithItemsDTO cart = cartService.getCartWithProductDetails(userId);
        return ResponseEntity.ok(cart);
    }

    // Thêm sản phẩm vào cart và trả về chi tiết cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(HttpServletRequest request, @RequestBody AddToCartRequest addToCartRequest) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse; // Trả về lỗi nếu userId không hợp lệ
        }
        Long userId = (Long) userIdResponse.getBody();

        // Thêm vào cart
        cartService.addToCart(userId, addToCartRequest.getProductId(), addToCartRequest.getQuantity());

        // Lấy chi tiết giỏ hàng để trả về
        CartWithItemsDTO detailedCart = cartService.getCartWithProductDetails(userId);
        return ResponseEntity.ok(detailedCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartWithItemsDTO> deleteCartItem(HttpServletRequest request, @PathVariable Long cartItemId) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(userIdResponse.getStatusCode()).body(null);
        }
        Long userId = (Long) userIdResponse.getBody();

        CartWithItems cartWithItems = cartService.deleteCartItem(userId, cartItemId);
        CartWithItemsDTO detailedCart = cartService.getCartWithProductDetails(userId);
        return ResponseEntity.ok(detailedCart);
    }

    @PostMapping("/items/{cartItemId}/increment")
    public ResponseEntity<?> incrementCartItemQuantity(HttpServletRequest request, @PathVariable Long cartItemId, @RequestParam(defaultValue = "1") int amount) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse;
        }
        Long userId = (Long) userIdResponse.getBody();

        try {
            CartWithItems cartWithItems = cartService.incrementCartItemQuantity(userId, cartItemId, amount);
            CartWithItemsDTO detailedCart = cartService.getCartWithProductDetails(userId);
            return ResponseEntity.ok(detailedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/items/{cartItemId}/decrement")
    public ResponseEntity<?> decrementCartItemQuantity(HttpServletRequest request, @PathVariable Long cartItemId, @RequestParam(defaultValue = "1") int amount) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse;
        }
        Long userId = (Long) userIdResponse.getBody();

        try {
            CartWithItems cartWithItems = cartService.decrementCartItemQuantity(userId, cartItemId, amount);
            CartWithItemsDTO detailedCart = cartService.getCartWithProductDetails(userId);
            return ResponseEntity.ok(detailedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}