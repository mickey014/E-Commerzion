package com.codewithkirk.productService.Controller;

import com.codewithkirk.productService.Dto.ProductPurchaseDto;
import com.codewithkirk.productService.Exception.ProductOrderServiceUnavailableException;
import com.codewithkirk.productService.Model.Products;
import feign.FeignException;
import org.springframework.web.bind.annotation.*;

import com.codewithkirk.productService.Dto.ProductDto;
import com.codewithkirk.productService.Exception.ProductException;
import com.codewithkirk.productService.Service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;
    
    @PostMapping("/products")
    public ResponseEntity<?> newProduct(@RequestBody ProductDto productDto) {
        try {
            productService.newProduct(productDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with Seller Service: " +  e.getMessage());
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/products/purchase")
    public ResponseEntity<?> productPurchase(@RequestBody ProductPurchaseDto productPurchaseDto) {
        try {
            productService.productPurchase(productPurchaseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with User Service: " +  e.getMessage());
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ProductOrderServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    @GetMapping("/products")
    public List<ProductDto> showProducts() {
        return productService.showProducts();
    }

    @GetMapping("/products/seller/{sellerId}")
    public ResponseEntity<?> showAllProductsBySellerId(@PathVariable Long sellerId) {
        try {
            List<Products> orders = productService.showAllProductsBySellerId(sellerId);
            return ResponseEntity.ok(orders);
        } catch (ProductException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> showProductById(@PathVariable Long productId) {
        try {
            Optional<Products> product = productService.showProductById(productId);
            Products foundProduct = product.get();
            ProductDto productDto = new ProductDto(
                    foundProduct.getProductId(),
                    foundProduct.getSellerId(),
                    foundProduct.getUserId(),
                    foundProduct.getProductName(),
                    foundProduct.getSkuCode(),
                    foundProduct.getProductDescription(),
                    foundProduct.getProductCategory(),
                    foundProduct.getPrice(),
                    foundProduct.getFeatures(),
                    foundProduct.getStock(),
                    foundProduct.getAvailability(),
                    foundProduct.getPhotoUrl()
            );
            return ResponseEntity.ok(productDto);
        } catch (ProductException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/products/seller/{sellerId}/product/{productId}")
    public ResponseEntity<?> showProductBySellerIdAndProductId(
                                    @PathVariable Long sellerId,
                                    @PathVariable Long productId) {

        try {
            Optional<Products> product = productService.showProductBySellerIdAndProductId(
                    sellerId, productId);
            Products foundProduct = product.get();
            ProductDto productDto = new ProductDto(
                    foundProduct.getProductId(),
                    foundProduct.getSellerId(),
                    foundProduct.getUserId(),
                    foundProduct.getProductName(),
                    foundProduct.getSkuCode(),
                    foundProduct.getProductDescription(),
                    foundProduct.getProductCategory(),
                    foundProduct.getPrice(),
                    foundProduct.getFeatures(),
                    foundProduct.getStock(),
                    foundProduct.getAvailability(),
                    foundProduct.getPhotoUrl()
            );
            return ResponseEntity.ok(productDto);
        } catch (ProductException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/products/user/{userId}/seller/{sellerId}/product/{productId}")
    public ResponseEntity<?> updateProduct( @PathVariable Long userId,
                                            @PathVariable Long sellerId,
                                            @PathVariable Long productId,
                                            @RequestBody ProductDto productDto) {
        try {
            // Call the service to update the user
            productService.updateProduct(userId, sellerId, productId, productDto);
            // Return the updated user as the response
            return ResponseEntity.ok(null);
        } catch (ProductException e) {
            // Handle FieldsException (validation errors)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with Seller Service: " +  e.getMessage());
        }catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/products/force/delete/user/{userId}/seller/{sellerId}/product/{productId}")
    public ResponseEntity<?> forceDeleteProduct(
                                                @PathVariable Long userId,
                                                @PathVariable Long sellerId,
                                                @PathVariable Long productId
                                                ) {
        try {
            productService.forceDeleteProduct(userId, sellerId, productId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // Return 404 with no body
        } catch (FeignException.NotFound e) {
            // Specific handling for 404 errors (Seller not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FeignException e) {
            // Handle other types of Feign errors (500 Internal Server Error, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error communicating with Seller Service: " +  e.getMessage());
        }catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/orderCheckService")
    public String checkOrderServiceHealth() {
        return productService.isOrderServiceUp() ? "Order Service is UP" : "Order Service is DOWN";
    }

    @GetMapping("/orderItemsCheckService")
    public String checkOrderItemsServiceHealth() {
        return productService.isOrderItemsServiceUp() ? "Order Items Service is UP" : "Order Items Service is DOWN";
    }

}
