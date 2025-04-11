package com.review.reviewservice.service;

import com.review.reviewservice.request.CreateReviewRequest;
import com.review.reviewservice.request.ReviewResponse;

import java.util.List;

public interface IReviewService {
    void createReview(Long userId, CreateReviewRequest request);

    List<ReviewResponse> getReviewsByProductId(Long productId);
}
