package com.codewithkirk.OrderItemsService.ServiceClient.Products;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsCombineDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductServiceClientFallback implements ProductServiceClient {
    @Override
    public OrderItemsCombineDto showProductById(Long productId) {
        return null;
    }
}
