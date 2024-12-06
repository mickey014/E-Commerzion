package com.codewithkirk.orderService.ServiceClient.Users;

import com.codewithkirk.orderService.Dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${external.api.userService.hostName}",
        url = "${external.api.userService.hostUrl}",
        fallback = UserServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    OrderDto getUserById(@PathVariable("userId") Long userId);
}
