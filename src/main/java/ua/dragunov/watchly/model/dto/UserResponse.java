package ua.dragunov.watchly.model.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String avatarUrl,
        LocalDateTime createdAt
) {}