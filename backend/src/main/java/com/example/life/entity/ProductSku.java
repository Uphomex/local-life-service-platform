
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
@TableName("product_sku")
public class ProductSku {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long productId;
    
    private String skuCode;
    
    private String specValues;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private String image;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
