package com.codewithkirk.OrderItemsService.ServiceClient.Products;

import com.codewithkirk.OrderItemsService.Dto.OrderItemsCombineDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "${external.api.productService.hostName}",
        url = "${external.api.productService.hostUrl}",
        fallback = ProductServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface ProductServiceClient {

    @GetMapping("/products/{productId}")
    OrderItemsCombineDto showProductById(@PathVariable("productId") Long productId);
}
