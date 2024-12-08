package com.codewithkirk.orderService.ServiceClient.Users;

import com.codewithkirk.orderService.Dto.OrderDto;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public OrderDto getUserById(Long userId) {
        return null;
    }
}
