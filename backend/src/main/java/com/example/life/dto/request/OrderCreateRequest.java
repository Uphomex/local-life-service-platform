
package com.example.life.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class OrderCreateRequest {
    
    @NotNull(message = "商家ID不能为空")
    private Long merchantId;
    
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;
    
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;
    
    @NotNull(message = "收货经度不能为空")
    private BigDecimal receiverLongitude;
    
    @NotNull(message = "收货纬度不能为空")
    private BigDecimal receiverLatitude;
    
    @NotNull(message = "订单商品不能为空")
    private List<OrderItemRequest> items;
    
    private String payMethod;
}
