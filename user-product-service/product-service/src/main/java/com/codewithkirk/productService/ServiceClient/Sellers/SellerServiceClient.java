package com.codewithkirk.productService.ServiceClient.Sellers;

import com.codewithkirk.productService.Dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${external.api.sellerService.hostName}",
        url = "${external.api.sellerService.hostUrl}",
        fallback = SellerServiceClientFallback.class) // Define service name and URL (URL can be external or internal)
public interface SellerServiceClient {

    @GetMapping("/sellers/{userId}/{sellerId}")
    ProductDto getSellerByUserIdAndSellerId(@PathVariable("userId") Long userId,
                                            @PathVariable("sellerId") Long sellerId);
}
