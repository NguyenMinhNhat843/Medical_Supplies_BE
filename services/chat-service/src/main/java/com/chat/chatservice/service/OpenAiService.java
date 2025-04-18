package com.chat.chatservice.service;

import com.chat.chatservice.entity.ChatEntity;
import com.chat.chatservice.model.ProductDTO;
import com.chat.chatservice.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenAiService {

    private final RestTemplate restTemplate;
    private final ChatRepository chatRepository;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    @Value("${product.service-url}")
    private String productServiceUrl;

    public OpenAiService(RestTemplate restTemplate, ChatRepository chatRepository) {
        this.restTemplate = restTemplate;
        this.chatRepository = chatRepository;
    }

    public Map<String, Object> ask(String userMessage) {
        List<ProductDTO> matchingProducts = fetchMatchingProducts(userMessage);
        boolean isProductRelated = !matchingProducts.isEmpty();

        StringBuilder productInfo = new StringBuilder();
        if (isProductRelated) {
            productInfo.append("Sản phẩm liên quan:");
            for (ProductDTO product : matchingProducts) {
                productInfo.append("\n- ")
                        .append(product.getName())
                        .append(": ")
                        .append(product.getDescription());
            }
        }

        String prompt = userMessage + (isProductRelated ? "\n\n" + productInfo : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                openaiApiUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseEntity.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        String aiResponse = message.get("content").toString();

        Map<String, Object> result = new HashMap<>();
        result.put("answer", aiResponse);
        if (isProductRelated) {
            result.put("products", matchingProducts);
        }

        return result;
    }


    private List<ProductDTO> fetchMatchingProducts(String userMessage) {
        String searchKeyword = userMessage.toLowerCase().trim();

        System.out.println("🔍 Keyword search gửi sang product-service: " + searchKeyword);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                .queryParam("keyword", searchKeyword);

        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProductDTO>>() {}
        );

        List<ProductDTO> products = response.getBody() != null ? response.getBody() : List.of();

        System.out.println("📦 Danh sách sản phẩm trả về: " + products.size());
        return products;
    }
}
