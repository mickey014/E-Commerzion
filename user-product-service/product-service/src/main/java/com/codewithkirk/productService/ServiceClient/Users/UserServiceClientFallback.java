package com.codewithkirk.productService.ServiceClient.Users;

import com.codewithkirk.productService.Model.Products;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public Products getUserById(Long userId) {
        return null;
    }
}
