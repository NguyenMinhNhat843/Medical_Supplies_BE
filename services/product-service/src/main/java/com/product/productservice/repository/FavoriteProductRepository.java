package com.product.productservice.repository;

import com.product.productservice.entity.FavoriteProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteProductRepository  extends JpaRepository<FavoriteProductEntity, Long> {
    List<FavoriteProductEntity> findByUserId(Long userId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
