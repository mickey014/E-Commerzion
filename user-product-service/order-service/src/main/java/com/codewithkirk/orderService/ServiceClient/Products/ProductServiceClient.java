package com.codewithkirk.orderService.ServiceClient.Products;

import com.codewithkirk.orderService.Dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${external.api.productService.hostName}",
        url = "${external.api.productService.hostUrl}",
        fallback = ProductServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface ProductServiceClient {

    @GetMapping("/products/{productId}")
    OrderDto showProductById(@PathVariable("productId") Long productId);
}
