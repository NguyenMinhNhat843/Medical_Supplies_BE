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
    private List<String> categories;    // để hiển thị tên category
    private String ingredient;
    private String usage;
    private String howToUse;
    private String sideEffects;
    private String caution;
    private String preservation;
}
