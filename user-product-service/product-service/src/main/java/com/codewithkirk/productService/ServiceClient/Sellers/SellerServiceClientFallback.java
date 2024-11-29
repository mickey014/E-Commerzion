package com.codewithkirk.productService.ServiceClient.Sellers;

import com.codewithkirk.productService.Dto.ProductDto;

import org.springframework.stereotype.Component;

@Component
public class SellerServiceClientFallback implements SellerServiceClient {
    @Override
    public ProductDto getSellerByUserIdAndSellerId(Long userId, Long sellerId) {
        return null;
    }
}
