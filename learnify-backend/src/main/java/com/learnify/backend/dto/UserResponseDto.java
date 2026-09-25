package com.learnify.backend.dto;

import com.learnify.backend.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String username;
    private String name;
    private String email;
    private UserRole userRole;
    private Long reputationPoints;
    private LocalDateTime createdAt;
}
