package ua.dragunov.watchly.model.dto;

import ua.dragunov.watchly.model.entity.Status;

public record WatchlistItemResponse(
        Long id,
        MediaItemPreviewResponse mediaItem,
        String status

) {}
