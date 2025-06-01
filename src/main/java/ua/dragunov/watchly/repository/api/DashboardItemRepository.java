package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.DashboardItem;

import java.util.List;

public interface DashboardItemRepository {
    List<DashboardItem> findByDashboardId(long dashboardId);

    DashboardItem save(DashboardItem item);

    void updatePosition(long dashboardId, long watchlistItemId, int newPosition);

    void delete(long dashboardId, long watchlistItemId);


}
