package ua.dragunov.watchly.model.dto;

public record UserProfileUpdateRequest(
        String email,
        String avatarUrl
) {}
