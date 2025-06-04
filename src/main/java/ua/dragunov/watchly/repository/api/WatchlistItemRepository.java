package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.WatchlistItem;

import java.util.List;
import java.util.Optional;

public interface WatchlistItemRepository {
    Optional<WatchlistItem> findById(long id);

    List<WatchlistItem> findByUserId(long userId);

    List<WatchlistItem> findByDashboard(long dashboardId);

    WatchlistItem save(WatchlistItem item);

    void delete(long id);

}
