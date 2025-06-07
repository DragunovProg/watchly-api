package ua.dragunov.watchly.model.dto;

import java.util.List;

public record MediaItemPreviewResponse(
        Long id,
        String title,
        String description,
        int releaseYear,
        String pictureUrl,
        List<GenreResponse> genres
) {}
