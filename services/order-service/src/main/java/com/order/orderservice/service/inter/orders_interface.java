package com.order.orderservice.service.inter;

import com.order.orderservice.entity.Order;

import java.util.List;
import java.util.Optional;

public interface orders_interface {
    List<Order> getAllOrders();

    Optional<Order> getOrderById(Integer id);

    List<Order> getOrdersByCustomerId(Integer customerId);

    Order createOrder(Order order);

    Order updateOrder(Integer id, Order updatedOrder);

    boolean deleteOrder(Integer id);
}
