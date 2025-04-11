package com.product.productservice.service;

import com.product.productservice.dto.ProductDTO;

import java.util.List;

public interface IProductService {
    ProductDTO createOrUpdateProduct(ProductDTO productDto);
    ProductDTO updateProduct(Long id, ProductDTO productDto);
    void deleteProduct(Long id);
    ProductDTO getProductById(Long id);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByCategory(Long categoryId);
}
