package com.product.productservice.config;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class RateLimitFilter implements Filter {

    @Autowired
    private Map<String, Bucket> buckets;

    @Autowired
    private Bucket rateLimitBucket;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String path = httpRequest.getRequestURI();
        if (path.contains("/search") || path.contains("/ai/search") || path.contains("/list")) {
            String clientKey = httpRequest.getHeader("X-UserId");
            if (clientKey == null || clientKey.isEmpty()) {
                clientKey = httpRequest.getRemoteAddr();
            }
            Bucket bucket = buckets.computeIfAbsent(clientKey, k -> rateLimitBucket);

            boolean consumed = bucket.tryConsume(1); // Chỉ gọi một lần
            System.out.println("Time now: " + System.currentTimeMillis() + ", Client: " + clientKey +
                    ", Tokens remaining before: " + bucket.getAvailableTokens() +
                    ", after: " + (consumed ? bucket.getAvailableTokens() : "blocked"));
            if (consumed) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("text/plain; charset=UTF-8");
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.getWriter().write("Quá số lượng request cho phép, vui lòng thử lại sau.");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}