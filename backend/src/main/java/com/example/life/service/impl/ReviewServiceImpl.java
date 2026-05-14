
package com.example.life.service.impl;

import com.example.life.common.ServiceException;
import com.example.life.entity.Order;
import com.example.life.entity.OrderItem;
import com.example.life.entity.Review;
import com.example.life.mapper.MerchantMapper;
import com.example.life.mapper.OrderItemMapper;
import com.example.life.mapper.OrderMapper;
import com.example.life.mapper.ReviewMapper;
import com.example.life.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final MerchantMapper merchantMapper;
    
    public ReviewServiceImpl(ReviewMapper reviewMapper, OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper, MerchantMapper merchantMapper) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.merchantMapper = merchantMapper;
    }
    
    @Override
    @Transactional
    public Review createReview(Long userId, Long orderId, Integer rating, String content, String images) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new ServiceException(403, "无权评价此订单");
        }
        
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new ServiceException(400, "订单未完成，无法评价");
        }
        
        List<Review> existingReviews = reviewMapper.selectList(
                reviewMapper.lambdaQuery().eq(Review::getOrderId, orderId));
        
        if (!existingReviews.isEmpty()) {
            throw new ServiceException(400, "该订单已评价");
        }
        
        Review review = Review.builder()
                .orderId(orderId)
                .userId(userId)
                .merchantId(order.getMerchantId())
                .rating(rating)
                .content(content)
                .images(images)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
        
        reviewMapper.insert(review);
        
        List<OrderItem> items = orderItemMapper.selectList(
                orderItemMapper.lambdaQuery().eq(OrderItem::getOrderId, orderId));
        
        for (OrderItem item : items) {
            Review productReview = Review.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .merchantId(order.getMerchantId())
                    .productId(item.getProductId())
                    .rating(rating)
                    .content(content)
                    .images(images)
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .build();
            reviewMapper.insert(productReview);
        }
        
        log.info("Review created: userId={}, orderId={}", userId, orderId);
        return review;
    }
    
    @Override
    public List<Review> getReviewsByMerchant(Long merchantId) {
        return reviewMapper.findByMerchantId(merchantId);
    }
    
    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewMapper.findByProductId(productId);
    }
    
    @Override
    public List<Review> getReviewsByUser(Long userId) {
        return reviewMapper.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public void replyReview(Long merchantId, Long reviewId, String reply) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new ServiceException(404, "评价不存在");
        }
        
        if (!review.getMerchantId().equals(merchantId)) {
            throw new ServiceException(403, "无权回复此评价");
        }
        
        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
        
        log.info("Review replied: reviewId={}, merchantId={}", reviewId, merchantId);
    }
    
    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new ServiceException(404, "评价不存在");
        }
        
        if (!review.getUserId().equals(userId)) {
            throw new ServiceException(403, "无权删除此评价");
        }
        
        reviewMapper.deleteById(reviewId);
        log.info("Review deleted: reviewId={}, userId={}", reviewId, userId);
    }
}
