
package com.example.life.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product_attribute")
public class ProductAttribute {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long productId;
    
    private String name;
    
    private String value;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
}
