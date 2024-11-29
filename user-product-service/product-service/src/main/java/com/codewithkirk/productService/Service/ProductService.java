package com.codewithkirk.productService.Service;

import com.codewithkirk.productService.Dto.ProductDto;
import com.codewithkirk.productService.Dto.ProductPurchaseDto;
import com.codewithkirk.productService.Model.Products;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Products newProduct(ProductDto productDto);
    List<ProductDto> showProducts();
    Optional<Products> showProductById(Long productId);

    List<Products> showAllProductsBySellerId(Long sellerId);
    Optional<Products> showProductBySellerIdAndProductId(Long sellerId, Long productId);

    Products updateProduct(Long userId, Long sellerId, Long productId, ProductDto productDto);

    void forceDeleteProduct(Long userId, Long sellerId, Long productId);


    void productPurchase(ProductPurchaseDto productPurchaseDto);

    boolean isOrderServiceUp();

    boolean isOrderItemsServiceUp();
}