package ua.dragunov.watchly.model.dto;

public record UserSummaryResponse(
        Long id,
        String username,
        String avatarUrl
) {}