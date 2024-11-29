package com.codewithkirk.productService.ServiceClient.Orders;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${external.api.orderService.hostName}",
        url = "${external.api.orderService.hostUrl}",
        fallback = OrderServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface OrderServiceClient {

    @GetMapping("/actuator/health")
    String checkHealth();
}
