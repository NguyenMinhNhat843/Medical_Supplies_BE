package com.apigateway.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.jwt.secretKey}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/**", "/users/**",
                        "/reviews/**","/chat/**","/api/products/**","/api/carts/**","/api/orders/**"))

                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll() // Cho phép toàn bộ auth
                        .pathMatchers("/users/**").permitAll() // Cho phép user
                        .pathMatchers("/api/products/**").permitAll() // Cho phép product
                        .pathMatchers("/api/category/**").permitAll() // Cho phép order
//                        .pathMatchers("/api/orders/**").permitAll() // Cho phép order
                        .pathMatchers("/reviews/**").permitAll() //
                        .pathMatchers("/chat/**").permitAll() //
//                        .pathMatchers("/api/carts/**").permitAll()
                        .pathMatchers("/api/cart-items/**").permitAll()
                        .pathMatchers("/api/inventory/**").permitAll() // Cho phép cart
                        .pathMatchers("/api/dashboard/**").permitAll() // Chỉ cho phép admi
                         .pathMatchers("/api/payments/**").permitAll() // Chỉ cho phép admin
                        .anyExchange().authenticated()         // Còn lại yêu cầu xác thực
                )
                // ⚠️ Đặt sau permitAll, JWT chỉ xử lý phần cần authenticated
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder())));

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        byte[] secretKeyBytes = Base64.getUrlDecoder().decode(jwtSecret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();
    }
}
