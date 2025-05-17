package com.product.productservice.repository;

import com.product.productservice.entity.ProductEntity;
import com.product.productservice.repository.repositorycustom.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,Long>, JpaSpecificationExecutor<ProductEntity>, ProductRepositoryCustom {
//    List<ProductEntity> findByNameContainingIgnoreCase(String name);
//    List<ProductEntity> findByNameContainingIgnoreCaseAndCategories_Id(String keyword, Long categoryId);
    @Query("SELECT p FROM ProductEntity p JOIN p.categories c WHERE c.id = :categoryId")
    List<ProductEntity> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM ProductEntity p JOIN p.categories c WHERE c.id = :categoryId AND p.name LIKE %:keyword%")
    List<ProductEntity> findByNameContainingAndCategoryId(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    List<ProductEntity> findByNameContaining(String keyword);
}
