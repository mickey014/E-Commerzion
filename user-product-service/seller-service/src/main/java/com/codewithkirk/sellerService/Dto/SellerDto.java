package com.codewithkirk.sellerService.Dto;

import com.codewithkirk.sellerService.Model.Sellers;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerDto {
    private Long sellerId;
    private Long userId;
    private String sellerName;
    private String storeName;
    private String email;
    private String phoneNumber;
    private String location;
    private Sellers.SellerStatus status;
    private String photoUrl;
}
