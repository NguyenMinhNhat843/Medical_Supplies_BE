package com.apigateway.apigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

//@Component
//public class JwtUtil {
//    private static final String SECRET_KEY = "key_secret";
//    private static final long EXPIRATION_TIME = 86400000; // 1 ngày
//
//    // Tạo JWT token
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//
//    // Giải mã JWT
//    public Claims extractClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // Kiểm tra token có hợp lệ không
//    public boolean validateToken(String token) {
//        try {
//            return extractClaims(token).getExpiration().after(new Date());
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
