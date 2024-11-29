package com.codewithkirk.OrderItemsService.ServiceClient.Users;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsCombineDto;
import com.codewithkirk.OrderItemsService.Dto.OrderItemsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${external.api.userService.hostName}",
        url = "${external.api.userService.hostUrl}",
        fallback = UserServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    OrderItemsCombineDto getUserById(@PathVariable("userId") Long userId);
}
