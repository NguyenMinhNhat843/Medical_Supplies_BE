package com.chat.chatservice.model;

import java.util.List;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double priceSupplied;
    private String image;
    private List<Long> categoryIds;
    private List<String> categories;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getPriceSupplied() { return priceSupplied; }
    public void setPriceSupplied(Double priceSupplied) { this.priceSupplied = priceSupplied; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public List<Long> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}
