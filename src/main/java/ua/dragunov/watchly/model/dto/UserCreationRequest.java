package ua.dragunov.watchly.model.dto;

public record UserCreationRequest(
        String username,
        String email,
        String password,
        String avatarUrl
) {}