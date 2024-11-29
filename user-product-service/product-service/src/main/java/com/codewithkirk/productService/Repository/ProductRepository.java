package com.codewithkirk.productService.Repository;

import com.codewithkirk.productService.Dto.ProductDto;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codewithkirk.productService.Model.Products;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, Long>{
    Optional<Products> findProductBySellerIdAndProductId(Long sellerId, Long productId);

    List<Products> findAllProductsBySellerId(Long sellerId);
}
