package com.codewithkirk.sellerService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Sellers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String sellerName;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String location;

    public enum SellerStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

    @Enumerated(EnumType.STRING)
    private SellerStatus status = SellerStatus.ACTIVE; // Default status is ACTIVE

    @Column(nullable = false, columnDefinition = "TEXT")
    private String photoUrl;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Removes milliseconds
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Removes milliseconds
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Removes milliseconds
    }
}
