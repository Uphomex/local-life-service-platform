
package com.example.life.dto.response;

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
public class ProductResponse {
    private Long id;
    private Long merchantId;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer soldCount;
    private String category;
    private String status;
    private List<AttributeResponse> attributes;
    private List<SkuResponse> skus;
}
