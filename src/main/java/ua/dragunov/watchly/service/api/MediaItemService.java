package ua.dragunov.watchly.service.api;

import ua.dragunov.watchly.model.dto.MediaItemCreationRequest;
import ua.dragunov.watchly.model.dto.MediaItemPreviewResponse;
import ua.dragunov.watchly.model.dto.MediaItemResponse;
import ua.dragunov.watchly.model.dto.MediaItemUpdateRequest;


import java.util.List;

public interface MediaItemService {
    MediaItemResponse create(MediaItemCreationRequest request);

    MediaItemResponse update(Long id, MediaItemUpdateRequest request);

    void delete(Long id);

    MediaItemResponse findById(Long id);

    List<MediaItemPreviewResponse> findAll();
}
