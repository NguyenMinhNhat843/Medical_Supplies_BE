package com.admindashboard.admindashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDetailDTO {
    private Long id;
    private Long productId;
    private int quantity;
    private LocalDateTime lastUpdate;
    private ProductDTO product;
}
