package ua.dragunov.watchly.model.dto;

public record ActorPreviewResponse(
        Long id,
        String firstName,
        String lastName,
        String photoUrl
) {}