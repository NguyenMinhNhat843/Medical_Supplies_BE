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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//    @Autowired
//    private UploadFileUtils uploadFileUtils;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private FavoriteProductRepository favoriteRepo;
    @Override
    public ProductDTO createOrUpdateProduct(ProductDTO productDto) {
        logger.info("[createOrUpdateProduct] Bắt đầu xử lý product với ID = {}", productDto.getId());

        ProductEntity product;
        if (productDto.getId() != null) {
            logger.debug("Đang cập nhật sản phẩm...");
            product = productRepository.findById(productDto.getId())
                    .map(existing -> productConverter.toEntity(productDto, existing))
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy sản phẩm với ID = {}", productDto.getId());
                        return new IllegalArgumentException("Product not found");
                    });
        } else {
            logger.debug("🆕 Đang tạo sản phẩm mới...");
            product = productConverter.toEntity(productDto);
        }

        ProductEntity saved = productRepository.save(product);
        logger.info("[createOrUpdateProduct] Đã lưu sản phẩm với ID = {}", saved.getId());

        return productConverter.convertToDto(saved);
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
        logger.warn("[deleteProduct] Xóa sản phẩm với ID = {}", id);
        productRepository.deleteById(id);

    }

    @Override
    public ProductDTO getProductById(Long id, Long userId) {
        logger.info("[getProductById] Tìm sản phẩm với ID = {}, UserID = {}", id, userId);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductDTO dto = productConverter.convertToDto(product);

        if (userId != null) {
            boolean isFav = favoriteRepo.existsByUserIdAndProductId(userId, id);
            dto.setIsFavorite(isFav);
            logger.debug("isFavorite = {}", isFav);
        }

        return dto;
    }

    @Retryable(
            value = { RuntimeException.class },  // hoặc SQLException, DataAccessException...
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)     // Retry mỗi 5 giây
    )
    @Override
    public List<ProductDTO> getAllProducts() {
        logger.info("[getAllProducts] Lấy toàn bộ sản phẩm...");
        return productRepository.findAll().stream()
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
    }


    // Lấy danh sách sản phẩm theo danh mục
    @Override
    public List<ProductDTO> getProductsByCategory(Long categoryId, Long userId) {
        logger.info("[getProductsByCategory] Lấy sản phẩm theo danh mục với ID = {}", categoryId);
        List<ProductEntity> products = productRepository.findByCategoryId(categoryId); // tùy bạn viết
        List<ProductDTO> dtos = products.stream().map(productConverter::convertToDto).toList();

        if (userId != null) {
            // Lấy danh sách ID sản phẩm đã được user yêu thích
            logger.info("[getProductsByCategory] Lấy danh sách sản phẩm yêu thích của user với ID = {}", userId);
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
        logger.info("[searchProductsByKeyword] Tìm sản phẩm với từ khóa: '{}'", keyword);

        if (keyword == null || keyword.isBlank()) {
            logger.warn("[searchProductsByKeyword] Từ khóa rỗng hoặc null");
            return Collections.emptyList();
        }

        // Normalize và tách từ
        String normalized = keyword.toLowerCase().replaceAll("[^\\p{L}\\p{Nd}\\s]", "");
        List<String> terms = Arrays.stream(normalized.split("\\s+"))
                .filter(term -> term.length() > 1)
                .toList();
        logger.debug("Các từ tìm kiếm sau khi xử lý: {}", terms);

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
        logger.info("[advancedSearchProducts] Bắt đầu tìm kiếm nâng cao với userId = {}", userId);

        List<ProductEntity> products = productRepository.advancedSearch(request);
        List<ProductDTO> result = products.stream()
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
        logger.debug("Số sản phẩm tìm thấy: {}", products.size());

        if (userId != null) {
            // Lấy danh sách ID sản phẩm đã được user yêu thích
            List<Long> favoriteIds = favoriteRepo.findByUserId(userId)
                    .stream()
                    .map(FavoriteProductEntity::getProductId)
                    .toList();

            // Gắn cờ isFavorite cho từng sản phẩm trong kết quả
            result.forEach(dto -> dto.setIsFavorite(favoriteIds.contains(dto.getId())));
            logger.debug("Đã đánh dấu isFavorite cho kết quả");

        }

        return result;
    }

    // Get filter options for advanced search
    @Override
    public Map<String, List<String>> getFilterOptions() {
        logger.info("[getFilterOptions] Lấy các lựa chọn filter nâng cao");

        return productRepository.getFilterOptions();
    }

    @Override
    public List<ProductDTO> getAllProductsWithFavorites(Long userId) {
        logger.info("[getAllProductsWithFavorites] Lấy danh sách sản phẩm với đánh dấu yêu thích (userId = {})", userId);
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
        logger.info("[getProductsByIds] Lấy danh sách sản phẩm theo ID: {}", ids);
        return productRepository.findAllById(ids).stream()
                .map(productConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long productId) {
        logger.info("[existsById] Kiểm tra sản phẩm với ID = {}", productId);
        return productRepository.existsById(productId);
    }

    @Retryable(
            value = { RuntimeException.class },  // hoặc SQLException, DataAccessException...
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)     // Retry mỗi 5 giây
    )
    @Override
    public List<ProductDTO> getAllProductsRetry() {
        logger.info("[getAllProductsRetry] Lấy toàn bộ sản phẩm với retry...");
        RetryContext context = RetrySynchronizationManager.getContext();
        int attempt = (context != null) ? context.getRetryCount() + 1 : 1;
        System.out.println("Thử lần thứ " + attempt);
        throw new RuntimeException("Fake lỗi DB để  retry!");
    }

    @Recover
    public List<ProductDTO> recoverAfterRetry(Exception ex) {
        System.err.println("Retry thất bại: " + ex.getMessage());
        return Collections.emptyList();
    }
}

