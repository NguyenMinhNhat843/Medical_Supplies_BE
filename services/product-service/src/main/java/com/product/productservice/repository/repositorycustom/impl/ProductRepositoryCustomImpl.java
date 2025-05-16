package com.product.productservice.repository.repositorycustom.impl;

import com.product.productservice.entity.ProductEntity;
import com.product.productservice.models.ProductSearchRequest;
import com.product.productservice.repository.repositorycustom.ProductRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Map<String, List<String>> getFilterOptions() {
        Map<String, List<String>> filters = new HashMap<>();

        // Nước sản xuất
        StringBuilder countryQuery = new StringBuilder("SELECT DISTINCT manufacturingCountry FROM product WHERE manufacturingCountry IS NOT NULL");
        List<String> countries = entityManager.createNativeQuery(countryQuery.toString()).getResultList();
        filters.put("manufacturingCountries", countries);

        // Thương hiệu
        StringBuilder manuQuery = new StringBuilder("SELECT DISTINCT manufacturer FROM product WHERE manufacturer IS NOT NULL");
        List<String> manufacturers = entityManager.createNativeQuery(manuQuery.toString()).getResultList();
        filters.put("manufacturers", manufacturers);

        // Nguồn gốc thương hiệu
        StringBuilder brandQuery = new StringBuilder("SELECT DISTINCT brandOrigin FROM product WHERE brandOrigin IS NOT NULL");
        List<String> brandOrigins = entityManager.createNativeQuery(brandQuery.toString()).getResultList();
        filters.put("brandOrigins", brandOrigins);

        return filters;
    }

    @Override
    public List<ProductEntity> advancedSearch(ProductSearchRequest request) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT p.* FROM product p ");

        boolean joinCategory = StringUtils.hasText(request.getCategoryName());
        if (joinCategory) {
            sql.append("JOIN productcategory pc ON p.id = pc.productid ");
            sql.append("JOIN category c ON pc.categoryid = c.id ");
        }

        sql.append("WHERE 1=1 ");

        if (joinCategory) {
            sql.append(" AND LOWER(c.name) = :categoryName ");
        }
        if (StringUtils.hasText(request.getKeyword())) {
            sql.append(" AND LOWER(p.name) LIKE :keyword ");
        }
        if (StringUtils.hasText(request.getManufacturer())) {
            sql.append(" AND LOWER(p.manufacturer) = :manufacturer ");
        }
        if (StringUtils.hasText(request.getManufacturingCountry())) {
            sql.append(" AND LOWER(p.manufacturingCountry) = :manufacturingCountry ");
        }
        if (StringUtils.hasText(request.getBrandOrigin())) {
            sql.append(" AND LOWER(p.brandOrigin) = :brandOrigin ");
        }
        if (StringUtils.hasText(request.getUsageProduct())) {
            sql.append(" AND LOWER(p.usageProduct) = :usageProduct ");
        }
        if (StringUtils.hasText(request.getPriceRange())) {
            String[] range = request.getPriceRange().split("-");
            if (range.length == 2) {
                sql.append(" AND p.price BETWEEN :minPrice AND :maxPrice ");
            }
        }

        if (StringUtils.hasText(request.getSortByPrice())) {
            if (request.getSortByPrice().equalsIgnoreCase("asc")) {
                sql.append(" ORDER BY p.price ASC ");
            } else if (request.getSortByPrice().equalsIgnoreCase("desc")) {
                sql.append(" ORDER BY p.price DESC ");
            }
        }

        Query query = entityManager.createNativeQuery(sql.toString(), ProductEntity.class);

        if (StringUtils.hasText(request.getCategoryName())) {
            query.setParameter("categoryName", request.getCategoryName().toLowerCase());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            query.setParameter("keyword", "%" + request.getKeyword().toLowerCase() + "%");
        }
        if (StringUtils.hasText(request.getManufacturer())) {
            query.setParameter("manufacturer", request.getManufacturer().toLowerCase());
        }
        if (StringUtils.hasText(request.getManufacturingCountry())) {
            query.setParameter("manufacturingCountry", request.getManufacturingCountry().toLowerCase());
        }
        if (StringUtils.hasText(request.getBrandOrigin())) {
            query.setParameter("brandOrigin", request.getBrandOrigin().toLowerCase());
        }
        if (StringUtils.hasText(request.getUsageProduct())) {
            query.setParameter("usageProduct", request.getUsageProduct().toLowerCase());
        }
        if (StringUtils.hasText(request.getPriceRange())) {
            String[] range = request.getPriceRange().split("-");
            if (range.length == 2) {
                try {
                    double min = Double.parseDouble(range[0]);
                    double max = Double.parseDouble(range[1]);
                    query.setParameter("minPrice", min);
                    query.setParameter("maxPrice", max);
                } catch (NumberFormatException ignored) {}
            }
        }

        return query.getResultList();

    }

    @Override
    public List<ProductEntity> searchByKeywordAndCategory(String keyword, Long categoryId) {
        return null;
    }
}
