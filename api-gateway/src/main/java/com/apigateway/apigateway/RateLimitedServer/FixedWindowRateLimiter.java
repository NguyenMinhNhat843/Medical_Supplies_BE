package com.apigateway.apigateway.RateLimitedServer;


import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiter<Object> {

    private final Map<String, RequestInfo> requestMap = new ConcurrentHashMap<>();

    private final int limit;
    private final long windowMs;

    public FixedWindowRateLimiter(int limit, long windowSeconds) {
        this.limit = limit;
        this.windowMs = windowSeconds * 1000;
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        long now = Instant.now().toEpochMilli();
        RequestInfo info = requestMap.getOrDefault(id, new RequestInfo(0, now));

        // Nếu đã qua 60s thì reset lại
        if (now - info.windowStart > windowMs) {
            info = new RequestInfo(0, now);
        }

        if (info.requestCount < limit) {
            info.requestCount++;
            requestMap.put(id, info);
            return Mono.just(new Response(true, getHeaders(limit - info.requestCount)));
        }

        return Mono.just(new Response(false, getHeaders(0)));
    }

    private Map<String, String> getHeaders(int remaining) {
        return Map.of(
                "X-RateLimit-Remaining", String.valueOf(remaining),
                "X-RateLimit-Limit", String.valueOf(limit)
        );
    }

    @Override
    public Map<String, Object> getConfig() {
        return Map.of();
    }

    @Override
    public Class<Object> getConfigClass() {
        return Object.class;
    }

    @Override
    public Object newConfig() {
        return null;
    }


    private static class RequestInfo {
        int requestCount;
        long windowStart;

        RequestInfo(int requestCount, long windowStart) {
            this.requestCount = requestCount;
            this.windowStart = windowStart;
        }
    }
}