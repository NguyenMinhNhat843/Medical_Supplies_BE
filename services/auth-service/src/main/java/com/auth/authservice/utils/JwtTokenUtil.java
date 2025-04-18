package com.auth.authservice.utils;


import com.auth.authservice.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;

import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    @Value("${spring.jwt.expiration}")
    private Long expiration;


    @Value("${spring.jwt.secretKey}")
    private String secretKey;

    // Tao token từ  username
    public String generateToken(UserEntity user) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        System.out.println("Generating Token for auth: " + user.getUsername());

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getId().toString())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new InvalidParameterException("Cannot create JWT token, error: " + e.getMessage());
        }
    }


    // Lay signkey
    private Key getSignKey(){
       byte[] bytes = Decoders.BASE64.decode(secretKey);
         return Keys.hmacShaKeyFor(bytes);
    }

    // Lấy toàn bộ claims
    private Claims extractAllClaims(String token){
       return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

   // Check experation
   public boolean isTokenExpired(String token) {
       Date expiration = this.extractClaim(token, Claims::getExpiration);
       return expiration.before(new Date());
   }
   // extract phone
    public String extractUsername(String token){
        return this.extractClaim(token,Claims::getSubject);
    }

// validate Token
public boolean validateToken(String token, UserDetails userDTO){
    String userName = extractUsername(token);
    return (userName.equals(userDTO.getUsername())) && !isTokenExpired(token);
}
}
