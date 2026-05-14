
package com.example.life.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuResponse {
    private Long id;
    private String skuCode;
    private String specValues;
    private BigDecimal price;
    private Integer stock;
    private String image;
}
