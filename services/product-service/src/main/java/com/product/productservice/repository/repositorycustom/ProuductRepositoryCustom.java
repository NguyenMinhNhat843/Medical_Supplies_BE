package com.product.productservice.repository.repositorycustom;

import com.product.productservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProuductRepositoryCustom {

    List<ProductEntity> searchByKeywordAndCategory(String keyword, Long categoryId);
}
