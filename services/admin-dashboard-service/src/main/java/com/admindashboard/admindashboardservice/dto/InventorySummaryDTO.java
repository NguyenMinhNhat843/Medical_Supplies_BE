package com.admindashboard.admindashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryDTO {
    private int totalProducts;
    private int lowStockProducts;
    private int outOfStockProducts;
    private double totalInventoryValue;
}
