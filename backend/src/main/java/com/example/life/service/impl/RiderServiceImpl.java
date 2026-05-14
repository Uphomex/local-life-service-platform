
package com.example.life.service.impl;

import com.example.life.common.ServiceException;
import com.example.life.dto.response.OrderResponse;
import com.example.life.entity.Order;
import com.example.life.entity.Rider;
import com.example.life.entity.User;
import com.example.life.mapper.OrderMapper;
import com.example.life.mapper.RiderMapper;
import com.example.life.mapper.UserMapper;
import com.example.life.service.OrderService;
import com.example.life.service.RiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiderServiceImpl implements RiderService {
    
    private final RiderMapper riderMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String RIDER_LOCATION_KEY = "rider:location:";
    
    public RiderServiceImpl(RiderMapper riderMapper, UserMapper userMapper,
                           OrderMapper orderMapper, OrderService orderService,
                           RedisTemplate<String, Object> redisTemplate) {
        this.riderMapper = riderMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public void registerRider(Long userId, String realName, String idCard) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(404, "用户不存在");
        }
        
        if (riderMapper.findByUserId(userId) != null) {
            throw new ServiceException(400, "该用户已注册为骑手");
        }
        
        Rider rider = Rider.builder()
                .userId(userId)
                .realName(realName)
                .idCard(idCard)
                .phone(user.getPhone())
                .status("PENDING")
                .longitude(BigDecimal.ZERO)
                .latitude(BigDecimal.ZERO)
                .orderCount(0)
                .rating(BigDecimal.ZERO)
                .earnings(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        riderMapper.insert(rider);
        
        user.setRole("RIDER");
        userMapper.updateById(user);
        
        log.info("Rider registered successfully: {}", userId);
    }
    
    @Override
    public void updateLocation(Long riderId, BigDecimal longitude, BigDecimal latitude) {
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            throw new ServiceException(404, "骑手不存在");
        }
        
        rider.setLongitude(longitude);
        rider.setLatitude(latitude);
        rider.setUpdatedAt(LocalDateTime.now());
        riderMapper.updateById(rider);
        
        String location = longitude + "," + latitude;
        redisTemplate.opsForValue().set(RIDER_LOCATION_KEY + riderId, location);
        
        log.info("Rider location updated: {} -> {},{}", riderId, longitude, latitude);
    }
    
    @Override
    public List<OrderResponse> getAssignedOrders(Long riderId) {
        if (riderMapper.selectById(riderId) == null) {
            throw new ServiceException(404, "骑手不存在");
        }
        
        return orderMapper.findByRiderId(riderId).stream()
                .filter(o -> !"COMPLETED".equals(o.getStatus()))
                .map(o -> orderService.getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void acceptOrder(Long riderId, Long orderId) {
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            throw new ServiceException(404, "骑手不存在");
        }
        
        if (!"ACTIVE".equals(rider.getStatus())) {
            throw new ServiceException(400, "骑手状态不可用");
        }
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        if (order.getRiderId() != null) {
            throw new ServiceException(400, "订单已被其他骑手接单");
        }
        
        orderService.assignRider(orderId, riderId);
        log.info("Rider {} accepted order {}", riderId, orderId);
    }
    
    @Override
    public void updateOrderStatus(Long riderId, Long orderId, String status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        if (!riderId.equals(order.getRiderId())) {
            throw new ServiceException(403, "无权操作此订单");
        }
        
        orderService.updateOrderStatus(orderId, status);
        
        if ("COMPLETED".equals(status)) {
            Rider rider = riderMapper.selectById(riderId);
            rider.setOrderCount(rider.getOrderCount() + 1);
            riderMapper.updateById(rider);
        }
    }
    
    @Override
    public void updateRiderStatus(Long riderId, String status) {
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            throw new ServiceException(404, "骑手不存在");
        }
        
        rider.setStatus(status);
        rider.setUpdatedAt(LocalDateTime.now());
        riderMapper.updateById(rider);
        
        log.info("Rider status updated: {} -> {}", riderId, status);
    }
}
