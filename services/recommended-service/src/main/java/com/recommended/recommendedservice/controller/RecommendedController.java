package com.recommended.recommendedservice.controller;

import com.recommended.recommendedservice.service.RecommendedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommended")
public class RecommendedController {

    @Autowired
    private RecommendedService recommendedService;
    
    @GetMapping("/popular")
    public ResponseEntity<List<Map<String, Object>>> getPopularProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendedService.getMostPopularProducts(limit));
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<List<Map<String, Object>>> getTopRatedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendedService.getTopRatedProducts(limit));
    }
    
    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<Map<String, Object>>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendedService.getSimilarProducts(productId, limit));
    }
    
    @GetMapping("/newest")
    public ResponseEntity<List<Map<String, Object>>> getNewestProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendedService.getNewestProducts(limit));
    }
    
    @PostMapping("/interaction")
    public ResponseEntity<String> saveInteraction(
            @RequestHeader("X-UserId") Long userId,
            @RequestParam Long productId,
            @RequestParam String interactionType,
            @RequestParam(required = false) Integer interactionValue) {
        
        recommendedService.saveInteraction(userId, productId, interactionType, interactionValue);
        return ResponseEntity.ok("Interaction saved successfully");
    }
}
