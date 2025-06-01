package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.WatchlistDashboard;

import java.util.List;
import java.util.Optional;

public interface WatchlistDashboardRepository {

    Optional<WatchlistDashboard> findById(long id);

    List<WatchlistDashboard> findByUserId(long userId);

    WatchlistDashboard save(WatchlistDashboard dashboard);

    void delete(long id);
}
