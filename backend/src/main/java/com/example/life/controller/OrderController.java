
package com.example.life.controller;

import com.example.life.common.ResponseResult;
import com.example.life.dto.request.OrderCreateRequest;
import com.example.life.dto.response.OrderResponse;
import com.example.life.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "订单接口", description = "订单管理相关接口")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping("/create")
    @Operation(summary = "创建订单", description = "用户创建订单")
    public ResponseEntity<ResponseResult<OrderResponse>> create(@Valid @RequestBody OrderCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        OrderResponse order = orderService.createOrder(userId, request);
        return ResponseEntity.ok(ResponseResult.success(order));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详情")
    public ResponseEntity<ResponseResult<OrderResponse>> getById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(ResponseResult.success(order));
    }
    
    @GetMapping("/user")
    @Operation(summary = "获取用户订单", description = "获取当前用户的订单列表")
    public ResponseEntity<ResponseResult<List<OrderResponse>>> getByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ResponseResult.success(orders));
    }
    
    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "获取商家订单", description = "获取商家的订单列表")
    public ResponseEntity<ResponseResult<List<OrderResponse>>> getByMerchant(@PathVariable Long merchantId) {
        List<OrderResponse> orders = orderService.getOrdersByMerchantId(merchantId);
        return ResponseEntity.ok(ResponseResult.success(orders));
    }
    
    @PutMapping("/{orderId}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    public ResponseEntity<ResponseResult<String>> updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ResponseResult.success("状态更新成功"));
    }
    
    @PutMapping("/{orderId}/complete")
    @Operation(summary = "完成订单", description = "骑手完成订单")
    public ResponseEntity<ResponseResult<String>> complete(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.ok(ResponseResult.success("订单已完成"));
    }
}
