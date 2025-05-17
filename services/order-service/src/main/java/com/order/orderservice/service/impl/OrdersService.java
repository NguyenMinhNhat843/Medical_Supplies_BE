package com.order.orderservice.service.impl;

import com.order.orderservice.client.CartClient;
import com.order.orderservice.dto.CartWithItemsDTO;
import com.order.orderservice.dto.DashboardStats;
import com.order.orderservice.entity.Order;
import com.order.orderservice.entity.OrderItem;
import com.order.orderservice.entity.OrderStatus;
import com.order.orderservice.entity.PaymentStatus;
import com.order.orderservice.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

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

    public Order updateOrder(Integer id, Order order) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        if (existingOrder != null) {
            existingOrder.setStatus(order.getStatus());
            existingOrder.setShippingAddress(order.getShippingAddress());
            existingOrder.setTotalAmount(order.getTotalAmount());
            existingOrder.setPaymentStatus(order.getPaymentStatus());
            existingOrder.setPaymentMethod(order.getPaymentMethod());
            existingOrder.setTrackingNumber(order.getTrackingNumber());
            return orderRepository.save(existingOrder);
        }
        return null;
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
}