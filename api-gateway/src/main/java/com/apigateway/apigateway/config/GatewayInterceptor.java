package com.apigateway.apigateway.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

public class GatewayInterceptor implements HandlerInterceptor {
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Chuyển tiếp request dựa vào đường dẫn
        String targetUrl = switch (path.split("/")[1]) {
            case "auth" -> "http://localhost:8081" + path;
            case "products" -> "http://localhost:8082" + path;
            case "orders" -> "http://localhost:8083" + path;
            default -> null;
        };

        if (targetUrl != null) {
            ResponseEntity<String> entity = restTemplate.getForEntity(targetUrl, String.class);
            response.setStatus(entity.getStatusCode().value());
            response.getWriter().write(entity.getBody());
            return false; // Chặn xử lý tiếp theo vì đã chuyển tiếp request
        }

        return true;
    }
}
