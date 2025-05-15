package com.order.orderservice.service.impl;

import com.order.orderservice.client.CartClient;
import com.order.orderservice.dto.CartWithItemsDTO;
import com.order.orderservice.entity.Order;
import com.order.orderservice.entity.OrderItem;
import com.order.orderservice.repository.OrderItemRepository;
import com.order.orderservice.repository.OrderRepository;
import com.order.orderservice.service.inter.orders_interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdersService implements orders_interface {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Integer id) {
        System.out.println("Fetching order with ID: " + id);
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByCustomerId(Integer customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Integer id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(updatedOrder.getStatus());
                    order.setShippingAddress(updatedOrder.getShippingAddress());
                    return orderRepository.save(order);
                }).orElse(null);
    }

    @Override
    public boolean deleteOrder(Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Order createOrderFromCart(Long userId) {
        CartWithItemsDTO cart = cartClient.getCartWithDetails(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống hoặc không tồn tại");
        }

        Order order = Order.builder()
                .customerId(userId.intValue())
                .orderDate(LocalDateTime.now())
                .status("PENDING")
                .shippingAddress("Địa chỉ mặc định")
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = cart.getItems().stream().map(item -> OrderItem.builder()
                .order(savedOrder)
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .priceAtTimeOfPurchase(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()).toList();

        orderItemRepository.saveAll(items);

        return savedOrder;
    }

}
