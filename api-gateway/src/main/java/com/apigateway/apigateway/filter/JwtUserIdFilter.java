package com.apigateway.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtUserIdFilter implements GlobalFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        System.out.println("✅ JwtUserIdFilter => Path: " + path); // In path đang xử lý
        return exchange.getPrincipal()
                .filter(principal -> principal instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(jwt -> jwt.getSubject()) // Hoặc jwt.getClaim("userId") nếu dùng custom claim
                .flatMap(userId -> {
                    System.out.println("✅ userId from token: " + userId);
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-UserId", userId)
                            .build();
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}