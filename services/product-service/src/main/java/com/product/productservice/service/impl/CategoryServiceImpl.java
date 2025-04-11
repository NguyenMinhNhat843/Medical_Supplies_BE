package com.product.productservice.service.impl;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.product.productservice.converter.ProductConverter;
import com.product.productservice.dto.CategoryDTO;
import com.product.productservice.repository.CategoryRepository;
import com.product.productservice.service.ICategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductConverter productConverter;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> productConverter.getModelMapper().map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> productConverter.getModelMapper().map(category, CategoryDTO.class))
                .orElse(null);
    }
}
