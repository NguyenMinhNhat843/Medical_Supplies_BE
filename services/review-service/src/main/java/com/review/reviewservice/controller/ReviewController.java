package com.review.reviewservice.controller;

import com.review.reviewservice.request.CreateReviewRequest;
import com.review.reviewservice.request.ReviewResponse;
import com.review.reviewservice.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;
    @PostMapping("/create")
    public ResponseEntity<?> createReview(
            @RequestHeader("X-UserId") String  userId,
            @RequestBody CreateReviewRequest request, @RequestHeader HttpHeaders headers
    ) {
        reviewService.createReview(Long.valueOf(userId), request);
        return ResponseEntity.ok("✅ Đánh giá đã được gửi thành công");
    }

    // Lấy danh sách review theo sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }
}
