package com.codewithkirk.orderService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Orders {

    @Id
    private String orderId;

    private Long customerId;

    private Long productId;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String orderStatus;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private String shippingMethod;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist() {
        orderAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Removes milliseconds
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Removes milliseconds
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Removes milliseconds
    }
}
