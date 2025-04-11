package com.product.productservice.converter;

import com.product.productservice.dto.ProductDTO;
import com.product.productservice.entity.CategoryEntity;
import com.product.productservice.entity.ProductEntity;
import com.product.productservice.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductConverter {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
    public ProductDTO convertToDto(ProductEntity product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        List<Long> categoryIds = product.getCategories().stream()
                .map(CategoryEntity::getId)
                .toList();
        dto.setCategoryIds(categoryIds);
        System.out.println("DEBUG DTO: " + dto);
        // ✅ Lấy danh sách tên danh mục
        List<String> categoryNames = product.getCategories().stream()
                .map(CategoryEntity::getName)
                .toList();
        dto.setCategories(categoryNames);

        return dto;
    }

    public ProductEntity toEntity(ProductDTO dto) {
        ProductEntity product = modelMapper.map(dto, ProductEntity.class);
        if (dto.getCategoryIds() != null) {
            List<CategoryEntity> categories = categoryRepository.findAllById(dto.getCategoryIds());
            product.setCategories(categories);
        }
        return product;
    }

    public ProductEntity toEntity(ProductDTO dto, ProductEntity existing) {
        modelMapper.map(dto, existing);
        if (dto.getCategoryIds() != null) {
            List<CategoryEntity> categories = categoryRepository.findAllById(dto.getCategoryIds());
            existing.setCategories(categories);
        }
        return existing;
    }
}



