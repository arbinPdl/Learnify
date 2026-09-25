package com.learnify.backend.entity;

import com.learnify.backend.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Long reputationPoints = 0L;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted = false;

    public User(String username, String password, String email, String name, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.userRole = userRole;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
