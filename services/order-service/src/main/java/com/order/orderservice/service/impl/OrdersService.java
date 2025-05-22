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
import com.order.orderservice.entity.Voucher;
import com.order.orderservice.repository.OrderRepository;
import com.order.orderservice.repository.VoucherRepository;
import com.order.orderservice.service.inter.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${payment-service.url}")
    private String paymentServiceUrl;

    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        logger.info("Fetched {} orders", orders.size());
        return orders;
    }

    public Order getOrderById(Integer id) {
        logger.info("Fetching order by id: {}", id);
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            logger.warn("Order not found with id: {}", id);
        } else {
            logger.info("Found order with id: {}", id);
        }
        return order;
    }

    public List<Order> getOrdersByCustomerId(Integer customerId) {
        logger.info("Fetching orders for customerId: {}", customerId);
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        logger.info("Fetched {} orders for customerId: {}", orders.size(), customerId);
        return orders;
    }

    public Order createOrder(Order order) {
        logger.info("Creating new order for customerId: {}", order.getCustomerId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created with id: {}", savedOrder.getId());
        return savedOrder;
    }

    public Order updateOrder(Integer orderId, Order orderDetails) {
        logger.info("Updating order with id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id: {}", orderId);
                    return new RuntimeException("Đơn hàng không tồn tại");
                });

        OrderStatus oldStatus = order.getStatus();
        logger.debug("Old status of order {} is {}", orderId, oldStatus);

        if (orderDetails.getTotalAmount() != null) {
            order.setTotalAmount(orderDetails.getTotalAmount());
        }
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

        Order updatedOrder = orderRepository.save(order);
        updatedOrder.setOldStatus(oldStatus);
        logger.info("Order with id: {} updated successfully", orderId);
        return updatedOrder;
    }

    public void deleteOrder(Integer id) {
        logger.info("Deleting order with id: {}", id);
        orderRepository.deleteById(id);
        logger.info("Order with id: {} deleted successfully", id);
    }

    public Order createOrderFromCart(Long userId, HttpServletRequest request) {
        logger.info("Creating order from cart for userId: {}", userId);
        CartWithItemsDTO cart = cartClient.getCartWithDetails(request);
        if (cart == null || cart.getItems().isEmpty()) {
            logger.error("Cart is empty or not found for userId: {}", userId);
            throw new RuntimeException("Giỏ hàng trống");
        }

        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        logger.debug("Calculated total amount: {}", totalAmount);

        Order order = Order.builder()
                .customerId(userId.intValue())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .totalAmount(totalAmount)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream().map(item -> OrderItem.builder()
                .productId(item.getProduct().getId())
                .quantity(item.getQuantity())
                .priceAtTimeOfPurchase(item.getProduct().getPrice())
                .order(order)
                .build()).collect(Collectors.toList());
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created with id: {} from cart for userId: {}", savedOrder.getId(), userId);
        return savedOrder;
    }

    public boolean updateCODStatus(Integer orderId) {
        logger.info("Updating COD status for orderId: {}", orderId);
        Optional<Order> optional = orderRepository.findById(orderId);
        if (optional.isEmpty()) {
            logger.warn("Order not found with id: {}", orderId);
            return false;
        }
        Order order = optional.get();
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setPaymentMethod("COD");
        orderRepository.save(order);
        logger.info("COD status updated for orderId: {}", orderId);
        return true;
    }

    public boolean updatePaymentInfo(Integer orderId, PaymentUpdateRequest req) {
        logger.info("Updating payment info for orderId: {}", orderId);
        Optional<Order> optional = orderRepository.findById(orderId);
        if (optional.isEmpty()) {
            logger.warn("Order not found with id: {}", orderId);
            return false;
        }

        Order order = optional.get();
        order.setPaymentMethod(req.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.valueOf(req.getPaymentStatus().toUpperCase()));
        order.setStatus(OrderStatus.valueOf(req.getStatus().toUpperCase()));
        orderRepository.save(order);
        logger.info("Payment info updated for orderId: {}", orderId);
        return true;
    }

    public DashboardStats getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching revenue stats from {} to {}", startDate, endDate);
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(7);
            logger.debug("Defaulting date range to last 7 days: {} to {}", startDate, endDate);
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        logger.debug("Fetched {} orders in date range", orders.size());

        double totalRevenue = orders.stream()
                .filter(order -> order.getStatus() != null && !order.getStatus().equals(OrderStatus.CANCELLED))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long orderCount = orders.stream()
                .filter(order -> order.getStatus() != null && !order.getStatus().equals(OrderStatus.CANCELLED))
                .count();

        logger.info("Calculated revenue: {}, order count: {}", totalRevenue, orderCount);
        return new DashboardStats(totalRevenue, orderCount);
    }

    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching orders from {} to {}", startDate, endDate);
        if (startDate == null || endDate == null) {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(7);
            logger.debug("Defaulting date range to last 7 days: {} to {}", startDate, endDate);
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        logger.info("Fetched {} orders in date range", orders.size());
        return orders;
    }

    @Autowired
    private VoucherService voucherService;

    public VoucherApplicationResponseDTO applyVoucherToOrder(Integer orderId, String voucherCode, HttpServletRequest request) {
        logger.info("Applying voucher {} to orderId: {}", voucherCode, orderId);
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            logger.error("Order not found with id: {}", orderId);
            throw new RuntimeException("Đơn hàng không tồn tại");
        }

        Voucher voucher = voucherService.getVoucherByCode(voucherCode)
                .orElseThrow(() -> {
                    logger.error("Voucher {} is invalid or expired", voucherCode);
                    return new RuntimeException("Voucher không hợp lệ hoặc đã hết hạn");
                });

        if (voucher.getUsedCount() >= voucher.getMaxUsage()) {
            logger.warn("Voucher {} has reached max usage limit", voucherCode);
            throw new RuntimeException("Voucher đã đạt giới hạn sử dụng");
        }

        double originalAmount = order.getTotalAmount();
        double discount = calculateDiscount(originalAmount, voucher);
        double finalAmount = originalAmount - discount;

        order.setTotalAmount(finalAmount);
        order.setVoucherCode(voucherCode);
        voucher.setUsedCount(voucher.getUsedCount() + 1);

        voucherService.updateVoucher(voucher.getId(), voucher);
        orderRepository.save(order);
        logger.info("Voucher {} applied to orderId: {}, discount: {}, final amount: {}", voucherCode, orderId, discount, finalAmount);

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
        logger.debug("Calculating discount for amount: {}, voucher: {}", amount, voucher.getCode());
        if ("PERCENTAGE".equals(voucher.getDiscountType())) {
            return amount * (voucher.getDiscountValue() / 100);
        } else {
            return voucher.getDiscountValue();
        }
    }
}