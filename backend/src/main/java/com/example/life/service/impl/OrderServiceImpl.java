
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

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final MerchantMapper merchantMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
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
    
    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        Merchant merchant = merchantMapper.selectById(request.getMerchantId());
        if (merchant == null) {
            throw new ServiceException(404, "商家不存在");
        }
        
        if (!"ACTIVE".equals(merchant.getStatus())) {
            throw new ServiceException(400, "商家未营业");
        }
        
        for (OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || !"ACTIVE".equals(product.getStatus())) {
                throw new ServiceException(404, "商品不存在或已下架");
            }
            
            if (!product.getMerchantId().equals(request.getMerchantId())) {
                throw new ServiceException(400, "商品不属于该商家");
            }
            
            if (!productService.lockStock(item.getProductId(), item.getQuantity())) {
                throw new ServiceException(400, "库存不足");
            }
        }
        
        String orderNo = generateOrderNo();
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        BigDecimal deliveryFee = merchant.getDeliveryFee();
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal payAmount = totalAmount.add(deliveryFee).subtract(discountAmount);
        
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .merchantId(request.getMerchantId())
                .totalAmount(totalAmount)
                .deliveryFee(deliveryFee)
                .discountAmount(discountAmount)
                .payAmount(payAmount)
                .status("PENDING")
                .payStatus("UNPAID")
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
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMapper.selectById(itemRequest.getProductId());
            
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
            
            product.setStock(product.getStock() - itemRequest.getQuantity());
            product.setSoldCount(product.getSoldCount() + itemRequest.getQuantity());
            productMapper.updateById(product);
        }
        
        kafkaTemplate.send("order_created", orderNo);
        log.info("Order created successfully: {}", orderNo);
        
        return getOrderById(order.getId());
    }
    
    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        List<OrderItem> items = orderItemMapper.selectList(
                orderItemMapper.lambdaQuery().eq(OrderItem::getOrderId, orderId));
        
        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        
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
    
    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderMapper.findByUserId(userId).stream()
                .map(o -> getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> getOrdersByMerchantId(Long merchantId) {
        return orderMapper.findByMerchantId(merchantId).stream()
                .map(o -> getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> getOrdersByRiderId(Long riderId) {
        return orderMapper.findByRiderId(riderId).stream()
                .map(o -> getOrderById(o.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        if ("PAID".equals(status)) {
            order.setPayStatus("PAID");
            order.setPayTime(LocalDateTime.now());
        } else if ("DELIVERING".equals(status)) {
            order.setDeliveryTime(LocalDateTime.now());
        } else if ("COMPLETED".equals(status)) {
            order.setCompleteTime(LocalDateTime.now());
        }
        
        orderMapper.updateById(order);
        kafkaTemplate.send("order_status_changed", order.getOrderNo() + ":" + status);
        log.info("Order status updated: {} -> {}", orderId, status);
    }
    
    @Override
    @Transactional
    public void assignRider(Long orderId, Long riderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        order.setRiderId(riderId);
        order.setStatus("DELIVERING");
        order.setDeliveryTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.updateById(order);
        kafkaTemplate.send("order_assigned", order.getOrderNo() + ":" + riderId);
        log.info("Order {} assigned to rider {}", orderId, riderId);
    }
    
    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(404, "订单不存在");
        }
        
        if (!"DELIVERING".equals(order.getStatus())) {
            throw new ServiceException(400, "订单状态不正确");
        }
        
        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.updateById(order);
        kafkaTemplate.send("order_completed", order.getOrderNo());
        log.info("Order completed: {}", orderId);
    }
    
    private String generateOrderNo() {
        return "LIFE" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
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
