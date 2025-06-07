package ua.dragunov.watchly.service.api;

import ua.dragunov.watchly.model.dto.GenreCreationRequest;
import ua.dragunov.watchly.model.dto.GenreResponse;
import ua.dragunov.watchly.model.dto.GenreUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface GenreService {

    Optional<GenreResponse> getGenreById(long id);

    List<GenreResponse> getAllGenres();

    GenreResponse createGenre(GenreCreationRequest request);

    GenreResponse updateGenre(long id, GenreUpdateRequest request);

    void deleteGenreById(long id);

    List<GenreResponse> getGenresByMediaItemId(long mediaItemId);

    Optional<GenreResponse> findByExternalIdAndSource(String externalId, long apiSourceId);

    void deleteAllByApiSourceId(long apiSourceId);

    GenreResponse getOrFetchByExternalId(String externalId, long apiSourceId);
}