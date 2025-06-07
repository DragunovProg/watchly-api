package ua.dragunov.watchly.model.dto;

public record MediaItemUpdateRequest(
        String title,
        String description,
        int releaseYear,
        String pictureUrl
) {}
