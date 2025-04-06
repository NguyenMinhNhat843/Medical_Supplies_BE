package com.admindashboard.admindashboardservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Cấu hình bảo mật (JWT)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
















