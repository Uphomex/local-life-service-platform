
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
@TableName("merchant")
public class Merchant {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String logo;
    
    private String address;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private String phone;
    
    private String category;
    
    private String businessLicense;
    
    private String status;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal minOrderAmount;
    
    private Integer deliveryTime;
    
    private BigDecimal rating;
    
    private Integer reviewCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
