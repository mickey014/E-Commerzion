package com.codewithkirk.userService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for row_id
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

    @Column(nullable = false, columnDefinition = "TEXT")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE; // Default status is ACTIVE

    @Column(columnDefinition = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP")
    // Set to DATETIME
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