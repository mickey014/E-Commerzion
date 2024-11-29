package com.codewithkirk.OrderItemsService.ServiceClient.Orders;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsCombineDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "${external.api.orderService.hostName}",
        url = "${external.api.orderService.hostUrl}",
        fallback = OrderServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface OrderServiceClient {

    @GetMapping("/orders/{orderId}")
    OrderItemsCombineDto getOrderById(@PathVariable("orderId") String orderId);
}
