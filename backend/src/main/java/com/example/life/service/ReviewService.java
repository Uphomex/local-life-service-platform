package com.example.life.service;

import com.example.life.entity.Review;

import java.util.List;

public interface ReviewService {
    
    Review createReview(Long userId, Long orderId, Integer rating, String content, String images);
    
    List<Review> getReviewsByMerchant(Long merchantId);
    
    List<Review> getReviewsByProduct(Long productId);
    
    List<Review> getReviewsByUser(Long userId);
    
    void replyReview(Long merchantId, Long reviewId, String reply);
    
    void deleteReview(Long userId, Long reviewId);
}
