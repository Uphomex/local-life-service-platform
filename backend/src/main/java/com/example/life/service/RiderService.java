
package com.example.life.service;

import com.example.life.dto.response.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

public interface RiderService {
    
    void registerRider(Long userId, String realName, String idCard);
    
    void updateLocation(Long riderId, BigDecimal longitude, BigDecimal latitude);
    
    List<OrderResponse> getAssignedOrders(Long riderId);
    
    void acceptOrder(Long riderId, Long orderId);
    
    void updateOrderStatus(Long riderId, Long orderId, String status);
    
    void updateRiderStatus(Long riderId, String status);
}
