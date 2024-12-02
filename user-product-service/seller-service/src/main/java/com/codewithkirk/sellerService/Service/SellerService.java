package com.codewithkirk.sellerService.Service;

import com.codewithkirk.sellerService.Dto.SellerDto;
import com.codewithkirk.sellerService.Model.Sellers;

import java.util.Optional;

public interface SellerService {

    Sellers registerSeller(SellerDto sellerDto);

    Optional<Sellers> getSellerByUserIdAndSellerId(Long userId, Long sellerId);

    Optional<Sellers> getSellerById(Long id);

    Sellers updateSeller(Long userId, Long sellerId, SellerDto sellerDto);

    Sellers safeDeleteSeller(Long userId, Long sellerId);

    void forceDeleteSeller(Long userId, Long sellerId);


}
