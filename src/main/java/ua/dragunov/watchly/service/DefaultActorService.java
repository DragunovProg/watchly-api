package ua.dragunov.watchly.service;

import ua.dragunov.watchly.model.dto.ActorResponse;
import ua.dragunov.watchly.model.entity.Actor;
import ua.dragunov.watchly.repository.api.ActorRepository;
import ua.dragunov.watchly.service.api.ActorService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class DefaultActorService implements ActorService {
    private final DataSource dataSource;
    private final ActorRepository actorRepository;

    public DefaultActorService(ActorRepository actorRepository, DataSource dataSource) {
        this.actorRepository = actorRepository;
        this.dataSource = dataSource;
    }


    @Override
    public Optional<ActorResponse> findById(Long id) {

        return Optional.empty();
    }

    @Override
    public List<ActorResponse> findByMediaItem(Long mediaItemId) {
        return List.of();
    }

    @Override
    public void create(ActorResponse actor) {

    }

    @Override
    public void update(ActorResponse actor) {

    }

    @Override
    public void delete(Long id) {

    }
}
