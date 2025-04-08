package com.product.productservice.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double priceSupplied;
    private String image;
    private List<Long> categoryIds;


}
