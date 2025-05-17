package com.order.orderservice.controller;

import com.order.orderservice.client.CartClient;
import com.order.orderservice.dto.CartWithItemsDTO;
import com.order.orderservice.entity.Order;
import com.order.orderservice.models.PaymentUpdateRequest;
import com.order.orderservice.service.impl.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrdersService orderService;

    @Autowired
    private CartClient cartClient; // Inject CartClient để lấy dữ liệu từ cart-service

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

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        System.out.println("Order ID: " + id);
        Optional<Order> order = Optional.ofNullable(orderService.getOrderById(id));
        System.out.println("Order: " + order);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/order")
    public ResponseEntity<?> getOrdersByCustomerId(HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse;
        }
        Long userId = (Long) userIdResponse.getBody();
        List<Order> orders = orderService.getOrdersByCustomerId(userId.intValue());
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createOrderAdmin(@RequestBody Order order, HttpServletRequest request) {
        // Giả sử chỉ admin có quyền tạo thủ công (cần thêm logic kiểm tra role)
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse;
        }
        Long userId = (Long) userIdResponse.getBody();
        // Kiểm tra quyền (giả định admin có role đặc biệt)
        if (!isAdmin(userId)) { // Hàm kiểm tra role (cần triển khai)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ admin được tạo đơn hàng thủ công");
        }
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    @PostMapping
    public ResponseEntity<?> createOrderFromCart(HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse;
        }
        Long userId = (Long) userIdResponse.getBody();

        // Lấy giỏ hàng từ cart-service
        CartWithItemsDTO cart = cartClient.getCartWithDetails(request);
        if (cart == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng trống hoặc không tồn tại");
        }

        // Tạo đơn hàng từ giỏ hàng
        Order order = orderService.createOrderFromCart(userId, request);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Tạo đơn hàng thất bại");
        }

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer id, HttpServletRequest request, @RequestBody Order order) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body(null);
        }
        Long userId = (Long) userIdResponse.getBody();
        Optional<Order> existingOrder = Optional.ofNullable(orderService.getOrderById(id));
        if (existingOrder.isPresent() && existingOrder.get().getCustomerId() != userId.intValue()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Order updatedOrder = orderService.updateOrder(id, order);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body("Thiếu hoặc không hợp lệ header X-UserId");
        }
        // Không cần sử dụng userId để kiểm tra quyền sở hữu
        Optional<Order> order = Optional.ofNullable(orderService.getOrderById(id));

        if (!order.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy đơn hàng có ID " + id);
        }

        // Thực hiện xóa
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Đơn hàng có ID " + id + " đã được xóa thành công.");
    }

    // Hàm kiểm tra role (giả định, cần triển khai thực tế)
    private boolean isAdmin(Long userId) {
        // Logic kiểm tra role (ví dụ: gọi service kiểm tra role từ token)
        return false; // Placeholder
    }


    // payment-service gọi đến hàm này để lấy thông tin đơn hàng
    @PutMapping("/{orderId}/cod-status")
    public ResponseEntity<?> updateOrderCODStatus(@PathVariable Integer orderId) {
        boolean updated = orderService.updateCODStatus(orderId);
        return updated
                ? ResponseEntity.ok("✅ Đã cập nhật đơn hàng sang COD: PENDING/UNPAID")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn hàng");
    }

    @PutMapping("/{orderId}/payment-info")
    public ResponseEntity<?> updatePaymentInfo(@PathVariable Integer orderId,
                                               @RequestBody PaymentUpdateRequest request) {
        boolean updated = orderService.updatePaymentInfo(orderId, request);
        return updated
                ? ResponseEntity.ok("✅ Cập nhật thanh toán đơn hàng thành công.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Không tìm thấy đơn hàng");
    }
}