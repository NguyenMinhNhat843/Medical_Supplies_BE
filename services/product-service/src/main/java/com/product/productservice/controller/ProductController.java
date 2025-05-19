package com.product.productservice.controller;

import com.product.productservice.dto.ProductDTO;
import com.product.productservice.models.ProductSearchRequest;
import com.product.productservice.service.IFavoriteService;
import com.product.productservice.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IFavoriteService favoriteService;
//    @PostMapping("/create")
//    public ResponseEntity<ProductDTO> createOrUpdate(@RequestBody ProductDTO productDto) {
//        return ResponseEntity.ok(productService.createOrUpdateProduct(productDto));
//    }

    // Admin có quyền tạo sản phẩm hoặc cập nhật sản phẩm
    @PostMapping("/create")
    public ResponseEntity<ProductDTO> createOrUpdate(
            @RequestHeader("X-Role") String role,
            @RequestBody ProductDTO productDto
    ) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // hoặc throw AccessDeniedException
        }

    return ResponseEntity.ok(productService.createOrUpdateProduct(productDto));
}

    // Admin xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader("X-Role") String role, @PathVariable Long id) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy thông tin sản phẩm theo id
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id, @RequestHeader(value = "X-UserId", required = false) Long userId) {
        return ResponseEntity.ok(productService.getProductById(id, userId));
    }

    // Lấy tất cả sản phẩm khongo co favorite
//    @GetMapping("/list")
//    public ResponseEntity<List<ProductDTO>> getAllProducts() {
//        return ResponseEntity.ok(productService.getAllProducts());
//    }

    // Lấy tất cả sản phẩm có favorite
    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestHeader(value = "X-UserId", required = false) Long userId
    ) {
        if (userId != null) {
            return ResponseEntity.ok(productService.getAllProductsWithFavorites(userId));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Them sản phẩm vào danh sách yêu thích
    @PostMapping("/favorite/{productId}")
    public ResponseEntity<String> addToFavorite(
            @RequestHeader("X-UserId") Long userId,
            @PathVariable Long productId
    ) {
        try {
            String msg = favoriteService.addToFavorite(userId, productId);
            return ResponseEntity.ok(msg);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    // Xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/favorite/{productId}")
    public ResponseEntity<String> removeFavorite(
            @RequestHeader("X-UserId") Long userId,
            @PathVariable Long productId
    ) {
        try {
            String msg = favoriteService.removeFromFavorite(userId, productId);
            return ResponseEntity.ok(msg);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // Danh sách yêu thích
    @GetMapping("/favorites")
    public ResponseEntity<List<ProductDTO>> getFavorites(@RequestHeader("X-UserId") Long userId) {
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }

    // Lấy tất cả sản phẩm theo danh mục
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId,  @RequestHeader(value = "X-UserId", required = false) Long userId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, userId));
    }


//    @GetMapping("/search")
//    public ResponseEntity<List<ProductDTO>> searchProducts(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String categoryName) {
//
//        List<ProductDTO> products = productService.searchProductsByNameAndCategory(keyword, categoryName);
//        return ResponseEntity.ok(products);
//    }

    // Tìm kiếm sản phẩm
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@ModelAttribute ProductSearchRequest request, @RequestHeader(value = "X-UserId", required = false) Long userId) {
        return ResponseEntity.ok(productService.advancedSearchProducts(request, userId));
    }

    // Chat-service gọi qua API
    @GetMapping("/ai/search")
    public ResponseEntity<List<ProductDTO>> searchByKeyword(@RequestParam String keyword) {
        String decodedKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
        System.out.println("Từ khóa đã decode: " + decodedKeyword);
        List<ProductDTO> results = productService.searchProductsByKeyword(decodedKeyword);
        return ResponseEntity.ok(results);
    }

    // Lọc sản phẩm theo các thuộc tính cho advanced search
    @GetMapping("/filters")
    public ResponseEntity<Map<String, List<String>>> getFilters() {
        return ResponseEntity.ok(productService.getFilterOptions());
    }
}

