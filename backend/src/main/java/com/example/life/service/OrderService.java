
package com.example.life.service;

import com.example.life.dto.request.OrderCreateRequest;
import com.example.life.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    
    OrderResponse createOrder(Long userId, OrderCreateRequest request);
    
    OrderResponse getOrderById(Long orderId);
    
    List<OrderResponse> getOrdersByUserId(Long userId);
    
    List<OrderResponse> getOrdersByMerchantId(Long merchantId);
    
    List<OrderResponse> getOrdersByRiderId(Long riderId);
    
    void updateOrderStatus(Long orderId, String status);
    
    void assignRider(Long orderId, Long riderId);
    
    void completeOrder(Long orderId);
}
