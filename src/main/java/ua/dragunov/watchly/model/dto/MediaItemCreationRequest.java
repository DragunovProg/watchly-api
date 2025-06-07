package ua.dragunov.watchly.model.dto;

public record MediaItemCreationRequest(
        String title,
        String description,
        int releaseYear,
        String pictureUrl,
        String externalId,
        Long apiSourceId
) {}
