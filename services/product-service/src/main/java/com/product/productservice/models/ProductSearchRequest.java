package com.product.productservice.models;


import lombok.Data;

@Data
public class ProductSearchRequest {
    private String keyword;
    private String categoryName;
    private String usageProduct;
    private String manufacturer;
    private String manufacturingCountry;
    private String priceRange; // "100000-300000"
    private String brandOrigin;
    private String sortByPrice; // "priceAsc", "priceDesc"
}
