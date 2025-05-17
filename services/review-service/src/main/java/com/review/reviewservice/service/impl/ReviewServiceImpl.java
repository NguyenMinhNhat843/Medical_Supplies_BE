package com.review.reviewservice.service.impl;

import com.review.reviewservice.entity.ReviewEntity;
import com.review.reviewservice.repository.ReviewRepository;
import com.review.reviewservice.request.CreateReviewRequest;
import com.review.reviewservice.request.ReviewResponse;
import com.review.reviewservice.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Override
    public void createReview(Long userId, CreateReviewRequest request) {
        ReviewEntity review = new ReviewEntity();
        review.setCustomerId(userId);
        review.setProductId(request.getProductId());
        review.setReviewText(request.getReviewText());
        review.setRating(request.getRating());
        reviewRepository.save(review);

    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        List<ReviewEntity> list = reviewRepository.findByProductId(productId);

        return list.stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getCustomerId(),
                        r.getProductId(),
                        r.getReviewText(),
                        r.getRating(),
                        r.getReviewDate()
                ))
                .collect(Collectors.toList());
    }
}
