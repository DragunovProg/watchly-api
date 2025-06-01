package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    Optional<Genre> findById(long id);

    Genre save(Genre genre);

    List<Genre> findAll();

    List<Genre> findByMediaItemId(long mediaItemId);

}
