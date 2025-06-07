package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.Actor;

import java.util.List;
import java.util.Optional;

public interface ActorRepository {
    Optional<Actor> findById(long id);

    List<Actor> findAll();

    Actor save(Actor actor);

    void deleteById(long id);

    List<Actor> findByMediaItemId(long mediaItemId);

}
