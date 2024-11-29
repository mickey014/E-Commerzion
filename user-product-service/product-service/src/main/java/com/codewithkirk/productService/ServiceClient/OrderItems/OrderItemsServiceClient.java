package com.codewithkirk.productService.ServiceClient.OrderItems;

import com.codewithkirk.productService.ServiceClient.Orders.OrderServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${external.api.orderItemsService.hostName}",
        url = "${external.api.orderItemsService.hostUrl}",
        fallback = OrderItemsServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface OrderItemsServiceClient {

    @GetMapping("/actuator/health")
    String checkHealth();
}
