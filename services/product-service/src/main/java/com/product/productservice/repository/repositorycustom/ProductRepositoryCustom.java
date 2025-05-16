package com.product.productservice.repository.repositorycustom;

import com.product.productservice.entity.ProductEntity;
import com.product.productservice.models.ProductSearchRequest;

import java.util.List;
import java.util.Map;

public interface ProductRepositoryCustom {
    Map<String, List<String>> getFilterOptions();
    List<ProductEntity> advancedSearch(ProductSearchRequest request);

    List<ProductEntity> searchByKeywordAndCategory(String keyword, Long categoryId);
}
