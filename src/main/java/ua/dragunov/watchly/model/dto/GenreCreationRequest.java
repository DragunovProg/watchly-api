package ua.dragunov.watchly.model.dto;

public record GenreCreationRequest(
        String name,
        String externalId,
        long apiSourceId
) {}