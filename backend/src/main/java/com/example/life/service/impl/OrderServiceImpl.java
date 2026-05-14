
/**
 * 订单服务实现类
 * 
 * 实现订单相关的业务逻辑：
 * 1. createOrder() - 创建订单
 * 2. getOrderById() - 根据ID获取订单详情
 * 3. getOrdersByUserId() - 获取用户订单列表
 * 4. getOrdersByMerchantId() - 获取商家订单列表
 * 5. getOrdersByRiderId() - 获取骑手订单列表
 * 6. updateOrderStatus() - 更新订单状态
 * 7. assignRider() - 分配骑手
 * 8. completeOrder() - 完成订单
 * 
 * 使用@Transactional注解管理事务，确保数据一致性。
 */
package com.example.life.service.impl;

import com.example.life.common.ServiceException;
import com.example.life.dto.request.OrderCreateRequest;
import com.example.life.dto.request.OrderItemRequest;
import com.example.life.dto.response.OrderItemResponse;
import com.example.life.dto.response.OrderResponse;
import com.example.life.entity.Merchant;
import com.example.life.entity.Order;
import com.example.life.entity.OrderItem;
import com.example.life.entity.Product;
import com.example.life.mapper.MerchantMapper;
import com.example.life.mapper.OrderItemMapper;
import com.example.life.mapper.OrderMapper;
import com.example.life.mapper.ProductMapper;
import com.example.life.service.OrderService;
import com.example.life.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 * 
 * @Slf4j 自动生成Logger对象
 * @Service 标识这是一个服务组件
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    // 订单数据访问层
    private final OrderMapper orderMapper;
    
    // 订单项数据访问层
    private final OrderItemMapper orderItemMapper;
    
    // 商家数据访问层
    private final MerchantMapper merchantMapper;
    
    // 商品数据访问层
    private final ProductMapper productMapper;
    
    // 商品服务（用于库存操作）
    private final ProductService productService;
    
    // Kafka消息发送模板
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    /**
     * 构造函数依赖注入
     */
    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                           MerchantMapper merchantMapper, ProductMapper productMapper,
                           ProductService productService, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.merchantMapper = merchantMapper;
        this.productMapper = productMapper;
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    /**
     * 创建订单
     * 
     * 创建订单流程：
     * 1. 验证商家是否存在且营业中
     * 2. 验证所有商品是否存在、在售且属于该商家
     * 3. 锁定库存（使用Redis分布式锁）
     * 4. 生成订单号
     * 5. 计算订单金额（商品总价 + 配送费 - 优惠）
     * 6. 创建订单主记录
     * 7. 创建订单项记录
     * 8. 扣减库存、增加销量
     * 9. 发送订单创建消息到Kafka
     * 
     * @param userId 用户ID
     * @param request 订单创建请求
     * @return 订单响应
     */
    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        // 1. 验证商家是否存在
        Merchant merchant = merchantMapper.selectById(request.getMerchantId());
        if (merchant == null) {
            throw new ServiceException(404, "商家不存在");
        }
        
        // 验证商家是否营业中
        if (!"ACTIVE".equals(merchant.getStatus())) {
            throw new ServiceException(400, "商家未营业");
        }
        
        // 2. 验证商品并锁定库存
        for (OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            
            // 验证商品是否存在且在售
            if (product == null || !"ACTIVE".equals(product.getStatus())) {
                throw new ServiceException(404, "商品不存在或已下架");
            }
            
            // 验证商品是否属于该商家
            if (!product.getMerchantId().equals(request.getMerchantId())) {
                throw new ServiceException(400, "商品不属于该商家");
            }
            
            // 3. 锁定库存（使用Redis分布式锁）
            if (!productService.lockStock(item.getProductId(), item.getQuantity())) {
                throw new ServiceException(400, "库存不足");
            }
        }
        
        // 4. 生成唯一订单号
        String orderNo = generateOrderNo();
        
        // 5. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // 计算最终支付金额（商品总价 + 配送费 - 优惠）
        BigDecimal deliveryFee = merchant.getDeliveryFee();
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal payAmount = totalAmount.add(deliveryFee).subtract(discountAmount);
        
        // 6. 创建订单主记录
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .merchantId(request.getMerchantId())
                .totalAmount(totalAmount)
                .deliveryFee(deliveryFee)
                .discountAmount(discountAmount)
                .payAmount(payAmount)
                .status("PENDING")           // 待支付
                .payStatus("UNPAID")        // 未支付
                .payMethod(request.getPayMethod() != null ? request.getPayMethod() : "WECHAT")
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())
                .receiverLongitude(request.getReceiverLongitude())
                .receiverLatitude(request.getReceiverLatitude())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        orderMapper.insert(order);
        
        // 7. 创建订单项记录并扣减库存
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMapper.selectById(itemRequest.getProductId());
            
            // 创建订单项
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(itemRequest.getProductId())
                    .skuId(itemRequest.getSkuId())
                    .productName(product.getName())
                    .skuSpec(itemRequest.getSkuSpec())
                    .price(product.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .totalAmount(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .image(product.getImage())
                    .createdAt(LocalDateTime.now())
                    .build();
            
            orderItemMapper.insert(orderItem);
            
            // 8. 扣减库存、增加销量
            product.setStock(product.getStock() - itemRequest.getQuantity());
            product.setSoldCount(product.getSoldCount() + itemRequest.getQuantity());
            productMapper.updateById(product);
        }
        
        // 9. 发送订单创建消息到Kafka（用于通知、统计等）
        kafkaTemplate.send("order_created", orderNo);
        log.info("Order created successfully: {}", orderNo);
        
        // 返回订单详情
        return getOrderById(order.getId());
    }
    
    /**
     * 根据ID获取订单详情
     * 
     * @param orderId 订单ID
     * @return 订单响应
     */
    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        // 获取订单项列表
        List<OrderItem> items = orderItemMapper.selectList(
                orderItemMapper.lambdaQuery().eq(OrderItem::getOrderId, orderId));
        
        // 获取商家信息
        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        
        // 构建响应
        return OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .merchantId(order.getMerchantId())
                .merchantName(merchant != null ? merchant.getName() : "")
                .riderId(order.getRiderId())
                .totalAmount(order.getTotalAmount())
                .deliveryFee(order.getDeliveryFee())
                .discountAmount(order.getDiscountAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .payStatus(order.getPayStatus())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .items(items.stream().map(this::toOrderItemResponse).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .payTime(order.getPayTime())
                .deliveryTime(order.getDeliveryTime())
                .completeTime(order.getCompleteTime())
                .build();
    }
    
    /**
     * 获取用户订单列表
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderMapper.findByUserId(userId).stream()
                .map(o -> getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取商家订单列表
     * 
     * @param merchantId 商家ID
     * @return 订单列表
     */
    @Override
    public List<OrderResponse> getOrdersByMerchantId(Long merchantId) {
        return orderMapper.findByMerchantId(merchantId).stream()
                .map(o -> getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取骑手订单列表
     * 
     * @param riderId 骑手ID
     * @return 订单列表
     */
    @Override
    public List<OrderResponse> getOrdersByRiderId(Long riderId) {
        return orderMapper.findByRiderId(riderId).stream()
                .map(o -> getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param status 新状态
     */
    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        // 更新状态和时间
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        // 根据状态设置对应的时间
        if ("PAID".equals(status)) {
            order.setPayStatus("PAID");
            order.setPayTime(LocalDateTime.now());
        } else if ("DELIVERING".equals(status)) {
            order.setDeliveryTime(LocalDateTime.now());
        } else if ("COMPLETED".equals(status)) {
            order.setCompleteTime(LocalDateTime.now());
        }
        
        orderMapper.updateById(order);
        
        // 发送状态变更消息到Kafka
        kafkaTemplate.send("order_status_changed", order.getOrderNo() + ":" + status);
        log.info("Order status updated: {} -> {}", orderId, status);
    }
    
    /**
     * 分配骑手
     * 
     * @param orderId 订单ID
     * @param riderId 骑手ID
     */
    @Override
    @Transactional
    public void assignRider(Long orderId, Long riderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        // 设置骑手ID并更新状态为配送中
        order.setRiderId(riderId);
        order.setStatus("DELIVERING");
        order.setDeliveryTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.updateById(order);
        
        // 发送骑手分配消息到Kafka
        kafkaTemplate.send("order_assigned", order.getOrderNo() + ":" + riderId);
        log.info("Order {} assigned to rider {}", orderId, riderId);
    }
    
    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     */
    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        // 验证订单状态必须是配送中
        if (!"DELIVERING".equals(order.getStatus())) {
            throw new ServiceException(400, "订单状态不正确");
        }
        
        // 更新状态为已完成
        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.updateById(order);
        
        // 发送订单完成消息到Kafka
        kafkaTemplate.send("order_completed", order.getOrderNo());
        log.info("Order completed: {}", orderId);
    }
    
    /**
     * 生成订单号
     * 
     * 格式：LIFE + 时间戳 + 8位随机字符
     * 
     * @return 订单号
     */
    private String generateOrderNo() {
        return "LIFE" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 订单项实体转响应DTO
     * 
     * @param item 订单项实体
     * @return 订单项响应
     */
    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .skuSpec(item.getSkuSpec())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalAmount(item.getTotalAmount())
                .image(item.getImage())
                .build();
    }
}
