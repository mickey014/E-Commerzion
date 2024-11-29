package com.codewithkirk.sellerService.Repository;

import com.codewithkirk.sellerService.Model.Sellers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Sellers, Long> {
    Optional<Sellers> findByEmail(String email);
    Optional<Sellers> findByPhoneNumber(String phoneNumber);
    Optional<Sellers> findSellerByUserIdAndSellerId(Long userId, Long sellerId);

    Optional<Sellers> findByUserId(Long seller);
}
