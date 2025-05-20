package com.apigateway.apigateway.config;

import com.apigateway.apigateway.RateLimitedServer.FixedWindowRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }

    @Bean
    @Primary
    public RateLimiter<Object> fixedWindowRateLimiter() {
        return new FixedWindowRateLimiter(5, 60); // 5 requests mỗi 60 giây
    }
}