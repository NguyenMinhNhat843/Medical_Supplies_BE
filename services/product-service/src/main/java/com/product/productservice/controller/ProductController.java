package com.product.productservice.controller;

import com.product.productservice.dto.ProductDTO;
import com.product.productservice.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductDTO> createOrUpdate(@RequestBody ProductDTO productDto) {
        return ResponseEntity.ok(productService.createOrUpdateProduct(productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }


    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName) {

        List<ProductDTO> products = productService.searchProductsByNameAndCategory(keyword, categoryName);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/ai/search")
    public ResponseEntity<List<ProductDTO>> searchByKeyword(@RequestParam String keyword) {
        String decodedKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
        System.out.println("📥 Từ khóa đã decode: " + decodedKeyword);
        List<ProductDTO> results = productService.searchProductsByKeyword(decodedKeyword);
        return ResponseEntity.ok(results);
    }
}

