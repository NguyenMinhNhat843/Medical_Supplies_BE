package com.product.productservice.repository.repositorycustom.impl;

import com.product.productservice.entity.ProductEntity;
import com.product.productservice.repository.repositorycustom.ProuductRepositoryCustom;
import com.product.productservice.service.impl.ProductServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProuductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<ProductEntity> searchByKeywordAndCategory(String keyword, Long categoryId) {
        StringBuilder sql = new StringBuilder();

        if (categoryId != null) {
            sql.append("SELECT DISTINCT p FROM ProductEntity p ");
            sql.append("JOIN p.categories c ");
            sql.append("WHERE LOWER(p.name) LIKE :keyword AND c.id = :categoryId");
        } else {
            sql.append("SELECT p FROM ProductEntity p ");
            sql.append("WHERE LOWER(p.name) LIKE :keyword");
        }

        // Chuẩn hóa từ khóa để tìm gần giống hơn (có thể mở rộng thành không dấu nếu cần)
        String normalizedKeyword = "%" + keyword.trim().toLowerCase() + "%";

        TypedQuery<ProductEntity> query = entityManager.createQuery(sql.toString(), ProductEntity.class);
        query.setParameter("keyword", normalizedKeyword);

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }

        return query.getResultList();

    }
}
