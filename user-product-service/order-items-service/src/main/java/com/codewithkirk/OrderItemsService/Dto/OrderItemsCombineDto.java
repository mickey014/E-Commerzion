package com.codewithkirk.OrderItemsService.Dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemsCombineDto {

    // Orders Details
    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("total_amount")
    private Double totalAmount;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("quantity")
    private String quantity;

    // User details
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("photo_url")
    private String photoUrl;

    @JsonProperty("status")
    private UserStatus status;

    // Enum for UserStatus
    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        DELETED
    }

    // Product Seller
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("seller_id")
    private Long sellerId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("sku_code")
    private String skuCode;

    @JsonProperty("product_description")
    private String productDescription;

    @JsonProperty("product_category")
    private String productCategory;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("features")
    private String features;

    @JsonProperty("stock")
    private Integer stock;

    @JsonProperty("availability")
    private String availability;
}
