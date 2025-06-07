package ua.dragunov.watchly.model.dto;

public record UserPasswordChangeRequest(
        String currentPassword,
        String newPassword
) {}