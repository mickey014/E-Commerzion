package com.codewithkirk.productService.ServiceClient.Orders;

import org.springframework.stereotype.Component;

@Component
public class OrderServiceClientFallback implements OrderServiceClient {
    @Override
    public String checkHealth() {
        return null;
    }
}
