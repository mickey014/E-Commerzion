package com.codewithkirk.sellerService.ServiceClient;

import com.codewithkirk.sellerService.Dto.UserDto;
import com.codewithkirk.sellerService.Model.Sellers;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public Sellers getUserById(Long userId) {
        return null;
    }
}
