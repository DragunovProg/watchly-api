package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.MediaItem;

import java.util.List;
import java.util.Optional;

public interface MediaItemRepository {
    Optional<MediaItem> findById(long id);

    List<MediaItem> findAll();

    MediaItem save(MediaItem mediaItem);

    void update(MediaItem mediaItem);

    void delete(long id);

}
