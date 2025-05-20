package com.recommended.recommendedservice.service;

import java.util.List;
import java.util.Map;

public interface RecommendedService {
    
    // Lấy sản phẩm phổ biến nhất
    List<Map<String, Object>> getMostPopularProducts(int limit);
    
    // Lấy sản phẩm được đánh giá cao nhất
    List<Map<String, Object>> getTopRatedProducts(int limit);
    
    // Lấy sản phẩm tương tự (cùng danh mục)
    List<Map<String, Object>> getSimilarProducts(Long productId, int limit);
    
    // Lấy sản phẩm mới nhất
    List<Map<String, Object>> getNewestProducts(int limit);
    
    // Lưu tương tác người dùng với sản phẩm
    void saveInteraction(Long userId, Long productId, String interactionType, Integer interactionValue);
}
