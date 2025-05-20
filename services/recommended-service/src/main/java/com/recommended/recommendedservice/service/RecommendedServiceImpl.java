package com.recommended.recommendedservice.service;

import com.recommended.recommendedservice.entity.Recommended;
import com.recommended.recommendedservice.repository.RecommendedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendedServiceImpl implements RecommendedService {

    @Autowired
    private RecommendedRepository recommendedRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String PRODUCT_SERVICE_URL = "http://PRODUCT-SERVICE/api/products";

    @Override
    public List<Map<String, Object>> getMostPopularProducts(int limit) {
        // Gọi đến product-service để lấy sản phẩm phổ biến nhất dựa trên số lượng bán
        String url = UriComponentsBuilder.fromHttpUrl(PRODUCT_SERVICE_URL)
                .queryParam("sortBy", "sales")
                .queryParam("limit", limit)
                .toUriString();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        return response.getBody() != null ? response.getBody() : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getTopRatedProducts(int limit) {
        // Lấy danh sách sản phẩm được đánh giá cao nhất
        // Có thể cần gọi đến review-service để lấy thông tin đánh giá
        // Hoặc dựa vào dữ liệu tương tác đã lưu trong RecommendedInteraction

        List<Object[]> topRatedProductIds = recommendedRepository.findMostPopularProductsByInteractionType("REVIEW");
        List<Map<String, Object>> result = new ArrayList<>();

        // Lấy thông tin chi tiết của sản phẩm từ product-service
        for (int i = 0; i < Math.min(limit, topRatedProductIds.size()); i++) {
            Object[] item = topRatedProductIds.get(i);
            Long productId = (Long) item[0];

            String url = PRODUCT_SERVICE_URL + "/" + productId;
            Map<String, Object> product = restTemplate.getForObject(url, Map.class);

            if (product != null) {
                result.add(product);
            }
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getSimilarProducts(Long productId, int limit) {
        // Lấy thông tin sản phẩm từ product-service
        String productUrl = PRODUCT_SERVICE_URL + "/" + productId;
        Map<String, Object> product = restTemplate.getForObject(productUrl, Map.class);

        if (product == null || product.get("categories") == null) {
            return new ArrayList<>();
        }

        // Lấy danh mục của sản phẩm
        List<Map<String, Object>> categories = (List<Map<String, Object>>) product.get("categories");
        if (categories.isEmpty()) {
            return new ArrayList<>();
        }

        // Lấy sản phẩm cùng danh mục
        String categoryName = (String) categories.get(0).get("name");
        String url = UriComponentsBuilder.fromHttpUrl(PRODUCT_SERVICE_URL + "/search")
                .queryParam("categoryName", categoryName)
                .queryParam("limit", limit)
                .toUriString();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> similarProducts = response.getBody() != null ? response.getBody() : new ArrayList<>();

        // Loại bỏ sản phẩm hiện tại khỏi danh sách
        similarProducts.removeIf(p -> p.get("id").equals(productId));

        return similarProducts.subList(0, Math.min(limit, similarProducts.size()));
    }

    @Override
    public List<Map<String, Object>> getNewestProducts(int limit) {
        // Gọi đến product-service để lấy sản phẩm mới nhất
        String url = UriComponentsBuilder.fromHttpUrl(PRODUCT_SERVICE_URL)
                .queryParam("sortBy", "createAt")
                .queryParam("limit", limit)
                .toUriString();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        return response.getBody() != null ? response.getBody() : new ArrayList<>();
    }

    @Override
    public void saveInteraction(Long userId, Long productId, String interactionType, Integer interactionValue) {
        Recommended interaction = new Recommended();
        interaction.setUserId(userId);
        interaction.setProductId(productId);
        interaction.setInteractionType(interactionType);
        interaction.setInteractionValue(interactionValue);

        recommendedRepository.save(interaction);
    }
}
