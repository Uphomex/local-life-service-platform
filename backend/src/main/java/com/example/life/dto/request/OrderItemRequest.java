
package com.example.life.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    private Long skuId;
    
    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须为正数")
    private Integer quantity;
    
    private String skuSpec;
}
