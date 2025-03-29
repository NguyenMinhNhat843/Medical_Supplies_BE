package com.apigateway.apigateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Cấu hình các route
//@Configuration
//public class GateWayConfig implements WebMvcConfigurer {
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public GateWayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtAuthenticationFilter).addPathPatterns("/**"); // Áp dụng JWT cho tất cả request
//    }
//}
