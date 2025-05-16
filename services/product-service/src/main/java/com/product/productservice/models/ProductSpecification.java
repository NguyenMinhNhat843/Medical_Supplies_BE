package com.product.productservice.models;

import com.product.productservice.entity.CategoryEntity;
import com.product.productservice.entity.ProductEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<ProductEntity> build(ProductSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getKeyword().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(request.getUsageProduct())) {
                predicates.add(cb.equal(cb.lower(root.get("usageProduct")), request.getUsageProduct().toLowerCase()));
            }

            if (StringUtils.hasText(request.getManufacturingCountry())) {
                predicates.add(cb.equal(cb.lower(root.get("manufacturingCountry")), request.getManufacturingCountry().toLowerCase()));
            }

            if (StringUtils.hasText(request.getManufacturer())) {
                predicates.add(cb.equal(cb.lower(root.get("manufacturer")), request.getManufacturer().toLowerCase()));
            }

            if (StringUtils.hasText(request.getCategoryName())) {
                Join<ProductEntity, CategoryEntity> categoryJoin = root.join("categories", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("name")), request.getCategoryName().toLowerCase()));
            }

            if (StringUtils.hasText(request.getPriceRange())) {
                String[] range = request.getPriceRange().split("-");
                if (range.length == 2) {
                    try {
                        double min = Double.parseDouble(range[0]);
                        double max = Double.parseDouble(range[1]);
                        predicates.add(cb.between(root.get("price"), min, max));
                    } catch (NumberFormatException ignored) {}
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
