
package com.example.life.controller;

import com.example.life.common.ResponseResult;
import com.example.life.dto.response.OrderResponse;
import com.example.life.service.RiderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rider")
@Tag(name = "骑手接口", description = "骑手管理相关接口")
public class RiderController {
    
    private final RiderService riderService;
    
    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }
    
    @PostMapping("/register")
    @Operation(summary = "骑手注册", description = "用户注册为骑手")
    public ResponseEntity<ResponseResult<String>> register(
            @RequestParam String realName,
            @RequestParam String idCard) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        
        riderService.registerRider(userId, realName, idCard);
        return ResponseEntity.ok(ResponseResult.success("注册成功，等待审核"));
    }
    
    @PutMapping("/{riderId}/location")
    @Operation(summary = "更新位置", description = "骑手更新位置信息")
    public ResponseEntity<ResponseResult<String>> updateLocation(
            @PathVariable Long riderId,
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude) {
        riderService.updateLocation(riderId, longitude, latitude);
        return ResponseEntity.ok(ResponseResult.success("位置更新成功"));
    }
    
    @GetMapping("/{riderId}/orders")
    @Operation(summary = "获取骑手订单", description = "获取骑手已接单的订单")
    public ResponseEntity<ResponseResult<List<OrderResponse>>> getAssignedOrders(@PathVariable Long riderId) {
        List<OrderResponse> orders = riderService.getAssignedOrders(riderId);
        return ResponseEntity.ok(ResponseResult.success(orders));
    }
    
    @PostMapping("/{riderId}/order/{orderId}/accept")
    @Operation(summary = "接单", description = "骑手接单")
    public ResponseEntity<ResponseResult<String>> acceptOrder(
            @PathVariable Long riderId,
            @PathVariable Long orderId) {
        riderService.acceptOrder(riderId, orderId);
        return ResponseEntity.ok(ResponseResult.success("接单成功"));
    }
    
    @PutMapping("/{riderId}/order/{orderId}/status")
    @Operation(summary = "更新订单状态", description = "骑手更新订单配送状态")
    public ResponseEntity<ResponseResult<String>> updateOrderStatus(
            @PathVariable Long riderId,
            @PathVariable Long orderId,
            @RequestParam String status) {
        riderService.updateOrderStatus(riderId, orderId, status);
        return ResponseEntity.ok(ResponseResult.success("状态更新成功"));
    }
    
    @PutMapping("/{riderId}/status")
    @Operation(summary = "更新骑手状态", description = "骑手更新在线状态")
    public ResponseEntity<ResponseResult<String>> updateStatus(
            @PathVariable Long riderId,
            @RequestParam String status) {
        riderService.updateRiderStatus(riderId, status);
        return ResponseEntity.ok(ResponseResult.success("状态更新成功"));
    }
}
