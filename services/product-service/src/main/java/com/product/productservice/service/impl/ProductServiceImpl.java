package com.product.productservice.service.impl;

import com.product.productservice.converter.ProductConverter;
import com.product.productservice.dto.ProductDTO;
import com.product.productservice.entity.CategoryEntity;
import com.product.productservice.entity.ProductEntity;
import com.product.productservice.models.ProductSearchRequest;
import com.product.productservice.models.ProductSpecification;
import com.product.productservice.repository.CategoryRepository;
import com.product.productservice.repository.ProductRepository;
import com.product.productservice.repository.repositorycustom.ProductRepositoryCustom;
import com.product.productservice.service.IProductService;
import com.product.productservice.utils.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepositoryCustom productRepositoryCustomImpl;

    @Autowired
    private ProductConverter productConverter;

    @Autowired
    private UploadFileUtils uploadFileUtils;
    @Override
    public ProductDTO createOrUpdateProduct(ProductDTO productDto)
    {
        ProductEntity product;

        if (productDto.getId() != null) {
            product = productRepository.findById(productDto.getId())
                    .map(existing -> productConverter.toEntity(productDto, existing))
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        } else {
            product = productConverter.toEntity(productDto);
        }


        if (productDto.getImageBase64() != null && productDto.getImageName() != null) {
            String path = "/product/" + productDto.getImageName();
            if (product.getImage() != null && !path.equals(product.getImage())) {
                uploadFileUtils.deleteFile(product.getImage());
            }

            byte[] bytes = Base64.decodeBase64(productDto.getImageBase64().getBytes());
            uploadFileUtils.writeOrUpdate(path, bytes);
            product.setImage(path);
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


    @Override
    public List<ProductDTO> searchProductsByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        // Normalize và tách từ
        String normalized = keyword.toLowerCase().replaceAll("[^\\p{L}\\p{Nd}\\s]", "");
        List<String> terms = Arrays.stream(normalized.split("\\s+"))
                .filter(term -> term.length() > 1)
                .toList();

        return productRepository.findAll().stream()
                .filter(product -> {
                    String name = product.getName().toLowerCase();
                    String desc = product.getDescription().toLowerCase();

                    return terms.stream().anyMatch(term ->
                            name.contains(term) || desc.contains(term));
                })
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> advancedSearchProducts(ProductSearchRequest request) {
        List<ProductEntity> products = productRepository.advancedSearch(request);
        return products.stream().map(productConverter::convertToDto).toList();
    }

    // Get filter options for advanced search
    @Override
    public Map<String, List<String>> getFilterOptions() {
        return productRepository.getFilterOptions();
    }
}

