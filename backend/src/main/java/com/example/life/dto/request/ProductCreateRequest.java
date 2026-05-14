
package com.example.life.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    private String description;
    
    private String image;
    
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotNull(message = "库存不能为空")
    private Integer stock;
    
    private String category;
    
    private Integer sortOrder;
    
    private List<AttributeRequest> attributes;
    
    private List<SkuRequest> skus;
}
