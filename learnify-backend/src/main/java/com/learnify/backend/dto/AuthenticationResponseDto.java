package com.learnify.backend.dto;

public record AuthenticationResponseDto(String token, Long userId, String username) {
}
