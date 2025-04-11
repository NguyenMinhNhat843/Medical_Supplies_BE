package com.product.productservice.service.impl;

import com.product.productservice.converter.ProductConverter;
import com.product.productservice.dto.ProductDTO;
import com.product.productservice.entity.CategoryEntity;
import com.product.productservice.entity.ProductEntity;
import com.product.productservice.repository.CategoryRepository;
import com.product.productservice.repository.ProductRepository;
import com.product.productservice.repository.repositorycustom.ProuductRepositoryCustom;
import com.product.productservice.repository.repositorycustom.impl.ProductRepositoryCustomImpl;
import com.product.productservice.service.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProuductRepositoryCustom productRepositoryCustomImpl;

    @Autowired
    private ProductConverter productConverter;
    @Override
    public ProductDTO createOrUpdateProduct(ProductDTO productDto)
    {
        ProductEntity product;
        if (productDto.getId() != null) {
            Optional<ProductEntity> existing = productRepository.findById(productDto.getId());
            if (existing.isPresent()) {
                product = productConverter.toEntity(productDto, existing.get());
            } else {
                throw new IllegalArgumentException("Product with ID " + productDto.getId() + " not found");
            }
        } else {
            product = productConverter.toEntity(productDto);
        }
        return productConverter.convertToDto(productRepository.save(product));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
//        ProductEntity product = productRepository.findById(id).orElse(null);
//        if (product != null) {
//            modelMapper.map(productDto, product);
//            if (productDto.getCategoryIds() != null) {
//                List<CategoryEntity> categories = categoryRepository.findAllById(productDto.getCategoryIds());
//                product.setCategories((List<CategoryEntity>) new HashSet<>(categories));
//            }
//            ProductEntity updatedProduct = productRepository.save(product);
//            return modelMapper.map(updatedProduct, ProductDTO.class);
//        }
        return null;
    }


    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);

    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productConverter::convertToDto)
                .orElse(null);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        Optional<CategoryEntity> category = categoryRepository.findById(categoryId);
        return category.map(cat -> cat.getProducts().stream()
                        .map(productConverter::convertToDto)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    public List<ProductDTO> searchProductsByName(String name) {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream()
                .filter(product -> product.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProductsByNameAndCategory(String keyword,String categoryName) {
        List<ProductEntity> products;

        if (categoryName != null && !categoryName.isBlank()) {
            CategoryEntity category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy category: " + categoryName));

            Long categoryId = category.getId();

            if (StringUtils.hasText(keyword)) {
                products = productRepository.findByNameContainingAndCategoryId(keyword, categoryId);
            } else {
                products = productRepository.findByCategoryId(categoryId);
            }

        } else {
            // Không chọn category → tìm toàn bộ
            if (StringUtils.hasText(keyword)) {
                products = productRepository.findByNameContaining(keyword);
            } else {
                products = productRepository.findAll();
            }
        }

        return products.stream()
                .map(productConverter::convertToDto)
                .toList();
    }
}

