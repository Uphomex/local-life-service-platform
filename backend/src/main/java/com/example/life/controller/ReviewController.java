
package com.example.life.controller;

import com.example.life.common.ResponseResult;
import com.example.life.entity.Review;
import com.example.life.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@Tag(name = "评价接口", description = "评价管理相关接口")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    @PostMapping("/create")
    @Operation(summary = "创建评价", description = "用户创建评价")
    public ResponseEntity<ResponseResult<Review>> create(
            @RequestParam Long orderId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        Review review = reviewService.createReview(userId, orderId, rating, content, images);
        return ResponseEntity.ok(ResponseResult.success(review));
    }
    
    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "获取商家评价", description = "获取商家的评价列表")
    public ResponseEntity<ResponseResult<List<Review>>> getByMerchant(@PathVariable Long merchantId) {
        List<Review> reviews = reviewService.getReviewsByMerchant(merchantId);
        return ResponseEntity.ok(ResponseResult.success(reviews));
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "获取商品评价", description = "获取商品的评价列表")
    public ResponseEntity<ResponseResult<List<Review>>> getByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(ResponseResult.success(reviews));
    }
    
    @GetMapping("/user")
    @Operation(summary = "获取用户评价", description = "获取当前用户的评价")
    public ResponseEntity<ResponseResult<List<Review>>> getByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        List<Review> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(ResponseResult.success(reviews));
    }
    
    @PutMapping("/merchant/{merchantId}/{reviewId}/reply")
    @Operation(summary = "回复评价", description = "商家回复评价")
    public ResponseEntity<ResponseResult<String>> reply(
            @PathVariable Long merchantId,
            @PathVariable Long reviewId,
            @RequestParam String reply) {
        reviewService.replyReview(merchantId, reviewId, reply);
        return ResponseEntity.ok(ResponseResult.success("回复成功"));
    }
    
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除评价", description = "用户删除自己的评价")
    public ResponseEntity<ResponseResult<String>> delete(@PathVariable Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(ResponseResult.success("删除成功"));
    }
}
