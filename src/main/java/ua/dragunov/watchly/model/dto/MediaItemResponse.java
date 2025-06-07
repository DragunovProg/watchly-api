package ua.dragunov.watchly.model.dto;

import java.util.List;

public record MediaItemResponse(
        Long id,
        String title,
        String description,
        int releaseYear,
        String pictureUrl,
        List<GenreResponse> genres,
        List<ActorPreviewResponse> actors
) {}
