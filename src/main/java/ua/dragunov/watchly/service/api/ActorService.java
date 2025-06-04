package ua.dragunov.watchly.service.api;

import ua.dragunov.watchly.model.dto.ActorResponse;
import ua.dragunov.watchly.model.entity.Actor;

import java.util.List;
import java.util.Optional;

public interface ActorService {
    Optional<ActorResponse> findById(Long id);

    List<ActorResponse> findByMediaItem(Long mediaItemId);

    void create(ActorResponse actor);

    void update(ActorResponse actor);

    void delete(Long id);
}
