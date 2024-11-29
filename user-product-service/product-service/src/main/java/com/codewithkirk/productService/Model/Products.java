package com.codewithkirk.productService.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String skuCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String productDescription;

    @Column(nullable = false)
    private String productCategory;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String features;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String availability;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP")
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