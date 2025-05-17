package com.apigateway.apigateway.utils;

//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.io.IOException;

//@Component
//public class JwtAuthenticationFilter implements HandlerInterceptor {
//    private final JwtUtil jwtUtil;
//
//    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false; // Chặn request
//        }
//
//        String token = authHeader.substring(7);
//        if (!jwtUtil.validateToken(token)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false; // Chặn request
//        }
//
//        Claims claims = jwtUtil.extractClaims(token);
//        request.setAttribute("username", claims.getSubject());
//
//        return true; // Tiếp tục xử lý request
//    }
//}
