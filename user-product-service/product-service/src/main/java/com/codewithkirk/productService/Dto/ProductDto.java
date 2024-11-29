package com.codewithkirk.productService.Dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long productId;
    private Long sellerId;
    private Long userId;
    private String productName;
    private String skuCode;
    private String productDescription;
    private String productCategory;
    private BigDecimal price;
    private String features;
    private Integer stock;
    private String availability;
    private String photoUrl;
}
