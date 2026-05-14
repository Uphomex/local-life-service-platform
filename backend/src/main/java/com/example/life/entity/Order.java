
package com.example.life.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("order")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long merchantId;
    
    private Long riderId;
    
    private BigDecimal totalAmount;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal discountAmount;
    
    private BigDecimal payAmount;
    
    private String status;
    
    private String payStatus;
    
    private String payMethod;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverAddress;
    
    private BigDecimal receiverLongitude;
    
    private BigDecimal receiverLatitude;
    
    private LocalDateTime payTime;
    
    private LocalDateTime deliveryTime;
    
    private LocalDateTime completeTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
