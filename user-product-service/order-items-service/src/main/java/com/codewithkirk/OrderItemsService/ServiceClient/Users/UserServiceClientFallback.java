package com.codewithkirk.OrderItemsService.ServiceClient.Users;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsCombineDto;
import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public OrderItemsCombineDto getUserById(Long userId) {
        return null;
    }
}
