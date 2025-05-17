package com.product.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private Double price;
    @Column(name = "priceSupplied")
    private Double priceSupplied;
    @Column(name = "image")
    private String image;

    @Column(name = "createat")
    @CurrentTimestamp
    private Date createAt;
    @Column(name = "updateat")
    @UpdateTimestamp
    private Date updateAt;

    @Column(name = "ingredient")
    private String ingredient;
    @Column(name = "usageProduct")
    private String usageProduct;
    @Column(name = "howToUse")
    private String howToUse;
    @Column(name = "sideEffects")
    private String sideEffects;
    @Column(name = "caution")
    private String caution;
    @Column(name = "preservation")
    private String preservation;

    @Column(name = "specifications")
    private String specifications;

    @Column(name = "brandOrigin")
    private String brandOrigin;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "manufacturingCountry")
    private String manufacturingCountry;

    @Column(name = "sales")
    private Long sales;

    @Column(name = "likes")
    private Long likes;

    @ManyToMany
    @JoinTable(
            name = "productcategory",
            joinColumns = @JoinColumn(name = "productid"),
            inverseJoinColumns = @JoinColumn(name = "categoryid")
    )
    private List<CategoryEntity> categories = new ArrayList<>();


    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Long getSales() {
        return sales;
    }

    public void setSales(Long sales) {
        this.sales = sales;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getBrandOrigin() {
        return brandOrigin;
    }

    public void setBrandOrigin(String brandOrigin) {
        this.brandOrigin = brandOrigin;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturerr(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturingCountry() {
        return manufacturingCountry;
    }

    public void setManufacturingCountry(String manufacturingCountry) {
        this.manufacturingCountry = manufacturingCountry;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPriceSupplied() {
        return priceSupplied;
    }

    public void setPriceSupplied(Double priceSupplied) {
        this.priceSupplied = priceSupplied;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getUsageProduct() {
        return usageProduct;
    }

    public void setUsageProduct(String usageProduct) {
        this.usageProduct = usageProduct;
    }

    public String getHowToUse() {
        return howToUse;
    }

    public void setHowToUse(String howToUse) {
        this.howToUse = howToUse;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public String getPreservation() {
        return preservation;
    }

    public void setPreservation(String preservation) {
        this.preservation = preservation;
    }
}
