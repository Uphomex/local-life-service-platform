
package com.example.life.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private String merchantName;
    private Long riderId;
    private String riderName;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private String status;
    private String payStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completeTime;
}
