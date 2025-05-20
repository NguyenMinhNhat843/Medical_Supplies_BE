package com.auth.authservice.service.serviceImpl;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailVerificationService {

    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public String generateToken(String email) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, email); // lưu token với email
        return token;
    }

    public boolean isValidToken(String token) {
        return tokenStore.containsKey(token);
    }

    public String getEmailFromToken(String token) {
        return tokenStore.get(token);
    }

    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }
}
