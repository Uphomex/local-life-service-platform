
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
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String skuSpec;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String image;
}
