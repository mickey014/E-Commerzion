package com.codewithkirk.productService.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {
    private String orderId;
    private Long customerId;
    private String orderStatus;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingAddress;
    private String shippingMethod;
    private BigDecimal totalAmount;
    private String trackingNumber;
    private List<OrderItemsDto> orderItems;  // List of order items

}
