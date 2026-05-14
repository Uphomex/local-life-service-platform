
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
@TableName("review")
public class Review {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long userId;
    
    private Long merchantId;
    
    private Long productId;
    
    private Integer rating;
    
    private String content;
    
    private String images;
    
    private String reply;
    
    private LocalDateTime replyTime;
    
    private String status;
    
    private LocalDateTime createdAt;
}
