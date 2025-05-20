package com.order.orderservice.service.impl;

import com.order.orderservice.client.CartClient;
import com.order.orderservice.dto.CartWithItemsDTO;
import com.order.orderservice.entity.Order;
import com.order.orderservice.entity.OrderItem;
import com.order.orderservice.entity.OrderStatus;
import com.order.orderservice.entity.PaymentStatus;
import com.order.orderservice.models.PaymentUpdateRequest;
import com.order.orderservice.dto.DashboardStats;
import com.order.orderservice.dto.VoucherApplicationResponseDTO;
import com.order.orderservice.entity.*;
import com.order.orderservice.repository.OrderRepository;
import com.order.orderservice.repository.VoucherRepository;
import com.order.orderservice.service.inter.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${payment-service.url}")
    private String paymentServiceUrl;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrdersByCustomerId(Integer customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        return orderRepository.save(order);
    }

    public Order updateOrder(Integer orderId, Order orderDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Giữ giá trị totalAmount hiện có nếu orderDetails không chứa totalAmount
        if (orderDetails.getTotalAmount() != null) {
            order.setTotalAmount(orderDetails.getTotalAmount());
        }

        // Cập nhật các trường khác
        if (orderDetails.getCustomerId() != null) {
            order.setCustomerId(orderDetails.getCustomerId());
        }
        if (orderDetails.getOrderDate() != null) {
            order.setOrderDate(orderDetails.getOrderDate());
        }
        if (orderDetails.getStatus() != null) {
            order.setStatus(orderDetails.getStatus());
        }
        if (orderDetails.getShippingAddress() != null) {
            order.setShippingAddress(orderDetails.getShippingAddress());
        }
        if (orderDetails.getPaymentStatus() != null) {
            order.setPaymentStatus(orderDetails.getPaymentStatus());
        }
        if (orderDetails.getPaymentMethod() != null) {
            order.setPaymentMethod(orderDetails.getPaymentMethod());
        }
        if (orderDetails.getTrackingNumber() != null) {
            order.setTrackingNumber(orderDetails.getTrackingNumber());
        }
        if (orderDetails.getVoucherCode() != null) {
            order.setVoucherCode(orderDetails.getVoucherCode());
        }

        return orderRepository.save(order);
    }

    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    public Order createOrderFromCart(Long userId, HttpServletRequest request) {
        // Lấy giỏ hàng từ cart-service
        CartWithItemsDTO cart = cartClient.getCartWithDetails(request);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Tính tổng giá
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Tạo Order
        Order order = Order.builder()
                .customerId(userId.intValue())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .totalAmount(totalAmount)
                .build();

        // Tạo danh sách OrderItem
        List<OrderItem> orderItems = cart.getItems().stream().map(item -> OrderItem.builder()
                .productId(item.getProduct().getId())
                .quantity(item.getQuantity())
                .priceAtTimeOfPurchase(item.getProduct().getPrice())
                .order(order)
                .build()).collect(Collectors.toList());
        order.setOrderItems(orderItems);

        // Lưu Order và OrderItems
        Order savedOrder = orderRepository.save(order);

        // (Tùy chọn) Làm rỗng giỏ hàng (cần gọi API của cart-service)
        // cartClient.clearCart(request); // Giả sử có phương thức này

        return savedOrder;
    }

    // Cập nhật trạng thái đơn hàng từ COD sang PENDING
    public boolean updateCODStatus(Integer orderId) {
        Optional<Order> optional = orderRepository.findById(orderId);
        if (optional.isEmpty()) return false;
        Order order = optional.get();
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setPaymentMethod("COD");
        orderRepository.save(order);
        return true;
    }

    public boolean updatePaymentInfo(Integer orderId, PaymentUpdateRequest req) {
        Optional<Order> optional = orderRepository.findById(orderId);
        if (optional.isEmpty()) return false;

        Order order = optional.get();
        order.setPaymentMethod(req.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.valueOf(req.getPaymentStatus().toUpperCase()));
        order.setStatus(OrderStatus.valueOf(req.getStatus().toUpperCase()));
        orderRepository.save(order);
        return true;
    }
    // Phương thức mới: Tính doanh thu và số lượng đơn hàng theo khoảng thời gian
    public DashboardStats getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        // Nếu không có ngày bắt đầu/kết thúc, mặc định lấy 7 ngày gần nhất
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now(); // Ngày hiện tại: 17/05/2025
            startDate = endDate.minusDays(7); // 7 ngày trước
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        // Lấy danh sách đơn hàng trong khoảng thời gian
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);

        // Tính tổng doanh thu và số lượng đơn hàng (loại bỏ đơn bị hủy)
        double totalRevenue = orders.stream()
                .filter(order -> order.getStatus() != null && !order.getStatus().equals(OrderStatus.CANCELLED))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long orderCount = orders.stream()
                .filter(order -> order.getStatus() != null && !order.getStatus().equals(OrderStatus.CANCELLED))
                .count();

        return new DashboardStats(totalRevenue, orderCount);
    }

    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(7);
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        return orderRepository.findByOrderDateBetween(start, end);
    }

    @Autowired
    private VoucherService voucherService;

    public VoucherApplicationResponseDTO applyVoucherToOrder(Integer orderId, String voucherCode, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) throw new RuntimeException("Đơn hàng không tồn tại");

        Voucher voucher = voucherService.getVoucherByCode(voucherCode) // Sửa: Dùng voucherCode thay vì id
                .orElseThrow(() -> new RuntimeException("Voucher không hợp lệ hoặc đã hết hạn"));

        if (voucher.getUsedCount() >= voucher.getMaxUsage()) {
            throw new RuntimeException("Voucher đã đạt giới hạn sử dụng");
        }

        double originalAmount = order.getTotalAmount();
        double discount = calculateDiscount(originalAmount, voucher);
        double finalAmount = originalAmount - discount;

        order.setTotalAmount(finalAmount);
        order.setVoucherCode(voucherCode);
        voucher.setUsedCount(voucher.getUsedCount() + 1);

        voucherService.updateVoucher(voucher.getId(), voucher); // Cập nhật số lần sử dụng
        orderRepository.save(order);

        return VoucherApplicationResponseDTO.builder()
                .orderId(order.getId())
                .originalAmount(originalAmount)
                .discountAmount(discount)
                .finalAmount(finalAmount)
                .voucherCode(voucherCode)
                .appliedAt(LocalDateTime.now())
                .build();
    }

    private double calculateDiscount(double amount, Voucher voucher) {
        if ("PERCENTAGE".equals(voucher.getDiscountType())) {
            return amount * (voucher.getDiscountValue() / 100);
        } else {
            return voucher.getDiscountValue();
        }

    }
}