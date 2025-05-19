package com.order.orderservice.dto;

public class DashboardStats {
    private double totalRevenue;
    private long orderCount;

    public DashboardStats(double totalRevenue, long orderCount) {
        this.totalRevenue = totalRevenue;
        this.orderCount = orderCount;
    }

    public double getTotalRevenue() { return totalRevenue; }
    public long getOrderCount() { return orderCount; }
}
