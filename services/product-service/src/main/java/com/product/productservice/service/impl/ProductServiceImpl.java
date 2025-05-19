package com.product.productservice.service.impl;

import com.product.productservice.converter.ProductConverter;
import com.product.productservice.dto.ProductDTO;
import com.product.productservice.entity.CategoryEntity;
import com.product.productservice.entity.FavoriteProductEntity;
import com.product.productservice.entity.ProductEntity;
import com.product.productservice.models.ProductSearchRequest;
import com.product.productservice.models.ProductSpecification;
import com.product.productservice.repository.CategoryRepository;
import com.product.productservice.repository.FavoriteProductRepository;
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


    @Autowired
    private FavoriteProductRepository favoriteRepo;
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
    public ProductDTO getProductById(Long id, Long userId) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductDTO dto = productConverter.convertToDto(product);

        if (userId != null) {
            boolean isFav = favoriteRepo.existsByUserIdAndProductId(userId, id);
            dto.setIsFavorite(isFav);
        }

        return dto;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
    }


    // Lấy danh sách sản phẩm theo danh mục
    @Override
    public List<ProductDTO> getProductsByCategory(Long categoryId, Long userId) {
        List<ProductEntity> products = productRepository.findByCategoryId(categoryId); // tùy bạn viết
        List<ProductDTO> dtos = products.stream().map(productConverter::convertToDto).toList();

        if (userId != null) {
            List<Long> favoriteIds = favoriteRepo.findByUserId(userId).stream()
                    .map(FavoriteProductEntity::getProductId).toList();
            dtos.forEach(p -> p.setIsFavorite(favoriteIds.contains(p.getId())));
        }
        return dtos;
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
    public List<ProductDTO> advancedSearchProducts(ProductSearchRequest request, Long userId) {
        List<ProductEntity> products = productRepository.advancedSearch(request);
        List<ProductDTO> result = products.stream()
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());

        if (userId != null) {
            // Lấy danh sách ID sản phẩm đã được user yêu thích
            List<Long> favoriteIds = favoriteRepo.findByUserId(userId)
                    .stream()
                    .map(FavoriteProductEntity::getProductId)
                    .toList();

            // Gắn cờ isFavorite cho từng sản phẩm trong kết quả
            result.forEach(dto -> dto.setIsFavorite(favoriteIds.contains(dto.getId())));
        }

        return result;
    }

    // Get filter options for advanced search
    @Override
    public Map<String, List<String>> getFilterOptions() {
        return productRepository.getFilterOptions();
    }

    @Override
    public List<ProductDTO> getAllProductsWithFavorites(Long userId) {
        List<Long> favoriteIds = favoriteRepo.findByUserId(userId).stream()
                .map(FavoriteProductEntity::getProductId)
                .toList();

        return productRepository.findAll().stream()
                .map(p -> {
                    ProductDTO dto = productConverter.convertToDto(p);
                    dto.setIsFavorite(favoriteIds.contains(p.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

    }

    @Override
    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long productId) {
        return productRepository.existsById(productId);
    }
}

