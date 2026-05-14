/**
 * 骑手服务实现类
 * 
 * 实现骑手相关的业务逻辑：
 * 1. registerRider() - 注册骑手
 * 2. updateLocation() - 更新骑手位置
 * 3. getAssignedOrders() - 获取已分配订单列表
 * 4. acceptOrder() - 接单
 * 5. updateOrderStatus() - 更新订单状态（骑手操作）
 * 6. updateRiderStatus() - 更新骑手状态
 * 
 * 使用Redis缓存骑手位置信息，支持实时定位功能。
 */
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

/**
 * 骑手服务实现
 * 
 * @Slf4j 自动生成Logger对象
 * @Service 标识这是一个服务组件
 */
@Slf4j
@Service
public class RiderServiceImpl implements RiderService {
    
    // 骑手数据访问层
    private final RiderMapper riderMapper;
    
    // 用户数据访问层
    private final UserMapper userMapper;
    
    // 订单数据访问层
    private final OrderMapper orderMapper;
    
    // 订单服务
    private final OrderService orderService;
    
    // Redis模板（用于骑手位置缓存）
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis键前缀常量
    private static final String RIDER_LOCATION_KEY = "rider:location:";  // 骑手位置缓存前缀
    
    /**
     * 构造函数依赖注入
     */
    public RiderServiceImpl(RiderMapper riderMapper, UserMapper userMapper,
                           OrderMapper orderMapper, OrderService orderService,
                           RedisTemplate<String, Object> redisTemplate) {
        this.riderMapper = riderMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * 注册骑手
     * 
     * 注册流程：
     * 1. 验证用户是否存在
     * 2. 验证用户是否已注册为骑手
     * 3. 创建骑手记录
     * 4. 更新用户角色为RIDER
     * 
     * @param userId 用户ID
     * @param realName 真实姓名
     * @param idCard 身份证号
     */
    @Override
    public void registerRider(Long userId, String realName, String idCard) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(404, "用户不存在");
        }
        
        // 验证用户是否已注册为骑手
        if (riderMapper.findByUserId(userId) != null) {
            throw new ServiceException(400, "该用户已注册为骑手");
        }
        
        // 创建骑手记录
        Rider rider = Rider.builder()
                .userId(userId)
                .realName(realName)
                .idCard(idCard)
                .phone(user.getPhone())
                .status("PENDING")         // 默认待审核状态
                .longitude(BigDecimal.ZERO)
                .latitude(BigDecimal.ZERO)
                .orderCount(0)
                .rating(BigDecimal.ZERO)
                .earnings(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        riderMapper.insert(rider);
        
        // 更新用户角色为骑手
        user.setRole("RIDER");
        userMapper.updateById(user);
        
        log.info("Rider registered successfully: {}", userId);
    }
    
    /**
     * 更新骑手位置
     * 
     * 同时更新数据库和Redis缓存，支持实时定位。
     * 
     * @param riderId 骑手ID
     * @param longitude 经度
     * @param latitude 纬度
     */
    @Override
    public void updateLocation(Long riderId, BigDecimal longitude, BigDecimal latitude) {
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            throw new ServiceException(404, "骑手不存在");
        }
        
        // 更新数据库位置
        rider.setLongitude(longitude);
        rider.setLatitude(latitude);
        rider.setUpdatedAt(LocalDateTime.now());
        riderMapper.updateById(rider);
        
        // 更新Redis缓存（格式：经度,纬度）
        String location = longitude + "," + latitude;
        redisTemplate.opsForValue().set(RIDER_LOCATION_KEY + riderId, location);
        
        log.info("Rider location updated: {} -> {},{}", riderId, longitude, latitude);
    }
    
    /**
     * 获取骑手已分配订单列表
     * 
     * @param riderId 骑手ID
     * @return 订单列表（排除已完成订单）
     */
    @Override
    public List<OrderResponse> getAssignedOrders(Long riderId) {
        // 验证骑手是否存在
        if (riderMapper.selectById(riderId) == null) {
            throw new ServiceException(404, "骑手不存在");
        }
        
        // 获取骑手的订单，排除已完成状态
        return orderMapper.findByRiderId(riderId).stream()
                .filter(o -> !"COMPLETED".equals(o.getStatus()))
                .map(o -> orderService.getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * 骑手接单
     * 
     * @param riderId 骑手ID
     * @param orderId 订单ID
     */
    @Override
    public void acceptOrder(Long riderId, Long orderId) {
        // 验证骑手是否存在
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            throw new ServiceException(404, "骑手不存在");
        }
        
        // 验证骑手状态是否可用
        if (!"ACTIVE".equals(rider.getStatus())) {
            throw new ServiceException(400, "骑手状态不可用");
        }
        
        // 验证订单是否存在
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        // 验证订单是否已被其他骑手接单
        if (order.getRiderId() != null) {
            throw new ServiceException(400, "订单已被其他骑手接单");
        }
        
        // 分配骑手
        orderService.assignRider(orderId, riderId);
        log.info("Rider {} accepted order {}", riderId, orderId);
    }
    
    /**
     * 骑手更新订单状态
     * 
     * @param riderId 骑手ID
     * @param orderId 订单ID
     * @param status 新状态
     */
    @Override
    public void updateOrderStatus(Long riderId, Long orderId, String status) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        // 验证骑手是否有权操作此订单
        if (!riderId.equals(order.getRiderId())) {
            throw new ServiceException(403, "无权操作此订单");
        }
        
        // 更新订单状态
        orderService.updateOrderStatus(orderId, status);
        
        // 如果订单完成，更新骑手订单数
        if ("COMPLETED".equals(status)) {
            Rider rider = riderMapper.selectById(riderId);
            rider.setOrderCount(rider.getOrderCount() + 1);
            riderMapper.updateById(rider);
        }
    }
    
    /**
     * 更新骑手状态
     * 
     * @param riderId 骑手ID
     * @param status 新状态（PENDING:待审核, ACTIVE:在线, OFFLINE:离线, SUSPENDED:暂停）
     */
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
