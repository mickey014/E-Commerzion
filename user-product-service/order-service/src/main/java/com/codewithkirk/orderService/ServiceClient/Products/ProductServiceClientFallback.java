package com.codewithkirk.orderService.ServiceClient.Products;

import com.codewithkirk.orderService.Dto.OrderDto;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceClientFallback implements ProductServiceClient {
    @Override
    public OrderDto showProductById(Long productId) {
        return null;
    }
}
