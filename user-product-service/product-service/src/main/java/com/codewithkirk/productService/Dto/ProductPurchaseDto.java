package com.codewithkirk.productService.Dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductPurchaseDto implements Serializable {
    private Long productId;
    private Long customerId;        // Customer ID who is purchasing the product
    private String orderId;
    private String orderStatus;
    private String shippingAddress; // Shipping address for the order
    private String shippingMethod;  // Shipping method chosen by the customer
    private String paymentMethod;     // Payment method chosen by the customer (could be a reference to a payment method)
    private String paymentStatus;   // Payment status (PAID/PENDING, etc.)
    private BigDecimal totalAmount;
    private Integer quantity;
    private List<ProductItemsDto> orderItems; // List of items to be purchase
}
