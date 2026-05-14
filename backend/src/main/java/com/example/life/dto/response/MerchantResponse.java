
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
public class MerchantResponse {
    private Long id;
    private String name;
    private String logo;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String phone;
    private String category;
    private String status;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private Integer deliveryTime;
    private BigDecimal rating;
    private Integer reviewCount;
}
