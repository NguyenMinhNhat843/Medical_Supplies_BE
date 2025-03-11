package com.user.userservice.utils;


import com.user.userservice.entity.UserEntity;
import com.user.userservice.model.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    public String generateToken(UserEntity user) throws Exception{

        Map<String,Object> claims = new HashMap<>();
        claims.put("id",user.getId());

       try {
           String token = Jwts.builder()
                   .setClaims(claims)
                   .setSubject(user.getUsername())
                   .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
           return token;
       }
       catch (Exception e){
              throw new InvalidParameterException("Cannot create jwt token, error: "+e.getMessage());
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
   public boolean expireToken(String token){
      Date experation = this.extractClaim(token,Claims::getExpiration);
      return    experation.before(new Date());
   }

   // extract phone
    public String extractUsernam(String token){
        return this.extractClaim(token,Claims::getSubject);
    }

// validate Token
public boolean valiateToken(String token, UserDTO userDTO){
    String userName = extractUsernam(token);
    return (userName.equals(userDTO.getUsername())) && !expireToken(token);
}
}
