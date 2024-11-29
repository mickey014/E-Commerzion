package com.codewithkirk.productService.ServiceClient.Users;

import com.codewithkirk.productService.Model.Products;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${external.api.userService.hostName}",
        url = "${external.api.userService.hostUrl}",
        fallback = UserServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    Products getUserById(@PathVariable("userId") Long userId);
}
