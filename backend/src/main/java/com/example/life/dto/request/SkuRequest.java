
package com.example.life.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuRequest {
    
    private String skuCode;
    
    @NotBlank(message = "规格值不能为空")
    private String specValues;
    
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    private BigDecimal price;
    
    @NotNull(message = "库存不能为空")
    private Integer stock;
    
    private String image;
}
