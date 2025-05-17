package com.product.productservice.service;

import com.product.productservice.dto.CategoryDTO;

import java.util.List;

public interface ICategoryService {
    // Get all categories
    List<CategoryDTO> getAllCategories();

    // Get category by id
    CategoryDTO getCategoryById(Long id);

}
