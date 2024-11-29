package com.codewithkirk.productService.Dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductItemsDto {
    private Long productId;          // The product ID
    private Integer quantity; // The quantity that the customer wants to purchase
    private BigDecimal unitPrice;        // The price of a single unit of the product
}
