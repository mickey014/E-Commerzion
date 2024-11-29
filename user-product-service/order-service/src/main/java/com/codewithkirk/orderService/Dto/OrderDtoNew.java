package com.codewithkirk.orderService.Dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDtoNew {
    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingMethod;
    private String trackingNumber;
    private Integer quantity;
    private List<OrderItemsDto> orderItems;
}
