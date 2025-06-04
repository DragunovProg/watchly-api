package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.Actor;
import ua.dragunov.watchly.model.entity.ApiSource;

import java.util.List;
import java.util.Optional;

public interface ApiSourceRepository {
    Optional<ApiSource> findById(long id);

    Optional<ApiSource> findByName(String name);

    List<ApiSource> findAll();

    ApiSource save(ApiSource apiSource);

}
