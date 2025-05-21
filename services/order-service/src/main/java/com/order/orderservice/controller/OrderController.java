package com.order.orderservice.controller;

import com.order.orderservice.client.CartClient;
import com.order.orderservice.dto.ApiResponseDTO;
import com.order.orderservice.dto.CartWithItemsDTO;
import com.order.orderservice.dto.DashboardStats;
import com.order.orderservice.dto.VoucherApplicationResponseDTO;
import com.order.orderservice.entity.Order;
import com.order.orderservice.models.PaymentUpdateRequest;
import com.order.orderservice.entity.Voucher;
import com.order.orderservice.repository.VoucherRepository;
import com.order.orderservice.service.impl.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrdersService orderService;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private RestTemplate restTemplate; // Thêm RestTemplate

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

    // Phương thức gửi thông báo đến notification-service
    private void sendNotification(Long userId, String message, String type) {
        try {
            String url = "http://localhost:8080/api/notifications/send?userId=" + userId + "&message=" + message + "&type=" + type;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);

            restTemplate.postForEntity(url, request, String.class);
            System.out.println("Sent notification to notification-service: userId=" + userId + ", message=" + message);
        } catch (Exception e) {
            System.err.println("Failed to send notification to notification-service: " + e.getMessage());
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
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse;
        }
        Long userId = (Long) userIdResponse.getBody();
        if (!isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ admin được tạo đơn hàng thủ công");
        }
        Order createdOrder = orderService.createOrder(order);

        // Gửi thông báo email khi tạo đơn hàng thành công
        sendNotification(userId, "Đơn hàng của bạn đã được tạo thành công! Trạng thái: " + createdOrder.getStatus(), "ORDER_PLACED");

        return ResponseEntity.ok(createdOrder);
    }

    @PostMapping
    public ResponseEntity<?> createOrderFromCart(HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return userIdResponse;
        }
        Long userId = (Long) userIdResponse.getBody();

        CartWithItemsDTO cart = cartClient.getCartWithDetails(request);
        if (cart == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng trống hoặc không tồn tại");
        }

        Order order = orderService.createOrderFromCart(userId, request);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Tạo đơn hàng thất bại");
        }

        // Gửi thông báo email khi tạo đơn hàng từ giỏ hàng thành công
        sendNotification(userId, "Đơn hàng của bạn đã được đặt thành công! Trạng thái: " + order.getStatus(), "ORDER_PLACED");

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Order>> updateOrder(
            @PathVariable Integer id, @RequestBody Order orderDetails) {
        try {
            Order updatedOrder = orderService.updateOrder(id, orderDetails);
            if (updatedOrder != null) {
                return ResponseEntity.ok(
                        ApiResponseDTO.<Order>builder()
                                .message("Cập nhật đơn hàng thành công")
                                .data(updatedOrder)
                                .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        ApiResponseDTO.<Order>builder()
                                .message("Cập nhật đơn hàng thất bại")
                                .data(null)
                                .build()
                );
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<Order>builder()
                            .message("Cập nhật thất bại: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body("Thiếu hoặc không hợp lệ header X-UserId");
        }
        Optional<Order> order = Optional.ofNullable(orderService.getOrderById(id));

        if (!order.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy đơn hàng có ID " + id);
        }

        orderService.deleteOrder(id);
        return ResponseEntity.ok("Đơn hàng có ID " + id + " đã được xóa thành công.");
    }

    // Hàm kiểm tra role (giả định, cần triển khai thực tế)
    private boolean isAdmin(Long userId) {
        return false; // Placeholder, cần triển khai logic kiểm tra role
    }

    @GetMapping("/dashboard-stats")
    public DashboardStats getDashboardStats(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return orderService.getRevenueByDateRange(startDate, endDate);
    }

    @GetMapping("/by-date")
    public List<Order> getOrdersByDateRange(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return orderService.getOrdersByDateRange(startDate, endDate);
    }

    @PostMapping("/{orderId}/apply-voucher")
    public ResponseEntity<ApiResponseDTO<VoucherApplicationResponseDTO>> applyVoucher(
            @PathVariable Integer orderId, @RequestParam String voucherCode, HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<VoucherApplicationResponseDTO>builder()
                            .message((String) userIdResponse.getBody())
                            .data(null)
                            .build()
            );
        }

        try {
            VoucherApplicationResponseDTO responseDTO = orderService.applyVoucherToOrder(orderId, voucherCode, request);
            if (responseDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponseDTO.<VoucherApplicationResponseDTO>builder()
                                .message("Đơn hàng không tồn tại")
                                .data(null)
                                .build()
                );
            }

            Long userId = (Long) userIdResponse.getBody();
            sendNotification(userId,
                    "Voucher " + voucherCode + " đã được áp dụng cho đơn hàng #" + orderId, "VOUCHER_APPLIED");

            return ResponseEntity.ok(
                    ApiResponseDTO.<VoucherApplicationResponseDTO>builder()
                            .message("Áp dụng voucher thành công")
                            .data(responseDTO)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponseDTO.<VoucherApplicationResponseDTO>builder()
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @Autowired
    private VoucherRepository voucherRepository;

    @PostMapping("/admin/voucher")
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher, HttpServletRequest request) {
        ResponseEntity<?> userIdResponse = extractUserId(request);
        if (userIdResponse.getStatusCode() != HttpStatus.OK) return userIdResponse;
        if (!isAdmin((Long) userIdResponse.getBody())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ admin được tạo voucher");
        }
        Voucher savedVoucher = voucherRepository.save(voucher);
        return ResponseEntity.ok(savedVoucher);
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
                ? ResponseEntity.ok("Cập nhật thanh toán đơn hàng thành công.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn hàng");
    }
}