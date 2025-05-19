package com.product.productservice.service;

import com.product.productservice.dto.ProductDTO;
import com.product.productservice.models.ProductSearchRequest;

import java.util.List;
import java.util.Map;

public interface IProductService {
    ProductDTO createOrUpdateProduct(ProductDTO productDto);
    ProductDTO updateProduct(Long id, ProductDTO productDto);
    void deleteProduct(Long id);
    ProductDTO getProductById(Long id, Long userId);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategory(Long categoryId, Long userId);

    List<ProductDTO> searchProductsByName(String name);

    List<ProductDTO> searchProductsByNameAndCategory(String keyword, String categoryName);

    List<ProductDTO> searchProductsByKeyword(String keyword);

    List<ProductDTO> advancedSearchProducts(ProductSearchRequest request, Long userId);
    Map<String, List<String>> getFilterOptions();

    List<ProductDTO> getAllProductsWithFavorites(Long userId);
    List<ProductDTO> getProductsByIds(List<Long> ids);
    boolean existsById(Long productId);
}
