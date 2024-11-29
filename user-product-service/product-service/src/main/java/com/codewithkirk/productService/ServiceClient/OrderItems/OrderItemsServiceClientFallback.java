package com.codewithkirk.productService.ServiceClient.OrderItems;

import com.codewithkirk.productService.ServiceClient.Orders.OrderServiceClient;
import org.springframework.stereotype.Component;

@Component
public class OrderItemsServiceClientFallback implements OrderItemsServiceClient {
    @Override
    public String checkHealth() {
        return null;
    }
}
