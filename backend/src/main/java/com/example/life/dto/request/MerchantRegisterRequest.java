
package com.example.life.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRegisterRequest {
    
    @NotBlank(message = "商家名称不能为空")
    private String name;
    
    @NotBlank(message = "地址不能为空")
    private String address;
    
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;
    
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;
    
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    
    @NotBlank(message = "分类不能为空")
    private String category;
    
    @NotBlank(message = "营业执照不能为空")
    private String businessLicense;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal minOrderAmount;
    
    private Integer deliveryTime;
}
