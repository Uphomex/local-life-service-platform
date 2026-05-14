
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
@TableName("order_item")
public class OrderItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long productId;
    
    private Long skuId;
    
    private String productName;
    
    private String skuSpec;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private BigDecimal totalAmount;
    
    private String image;
    
    private LocalDateTime createdAt;
}
