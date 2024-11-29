package com.codewithkirk.OrderItemsService.ServiceClient.Orders;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsCombineDto;
import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderServiceClientFallback implements OrderServiceClient {
    @Override
    public OrderItemsCombineDto getOrderById(String orderId) {
        return null;
    }
}
