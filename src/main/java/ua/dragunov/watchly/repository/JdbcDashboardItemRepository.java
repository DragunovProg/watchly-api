package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.DashboardItem;
import ua.dragunov.watchly.repository.api.DashboardItemRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcDashboardItemRepository implements DashboardItemRepository {



    private final DataSource dataSource;

    public JdbcDashboardItemRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<DashboardItem> findByDashboardId(long dashboardId) {
        List<DashboardItem> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DashboardItemSqlQueries.SQL_FIND_BY_DASHBOARD_ID)) {
            stmt.setLong(1, dashboardId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DashboardItem item = new DashboardItem();
                    item.setDashboardId(rs.getLong(DashboardItemSqlQueries.COLUMN_DASHBOARD_ID));
                    item.setWatchlistItemId(rs.getLong(DashboardItemSqlQueries.COLUMN_WATCHLIST_ITEM_ID));
                    item.setPosition(rs.getInt(DashboardItemSqlQueries.COLUMN_POSITION));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding dashboard items for dashboard id: " + dashboardId, e);
        }
        return items;
    }

    @Override
    public DashboardItem save(DashboardItem item) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DashboardItemSqlQueries.SQL_INSERT)) {
            stmt.setLong(1, item.getDashboardId());
            stmt.setLong(2, item.getWatchlistItemId());
            stmt.setInt(3, item.getPosition());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving dashboard item", e);
        }
        return item;
    }

    @Override
    public void updatePosition(long dashboardId, long watchlistItemId, int newPosition) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DashboardItemSqlQueries.SQL_UPDATE_POSITION)) {
            stmt.setInt(1, newPosition);
            stmt.setLong(2, dashboardId);
            stmt.setLong(3, watchlistItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating dashboard item position", e);
        }
    }

    @Override
    public void delete(long dashboardId, long watchlistItemId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DashboardItemSqlQueries.SQL_DELETE)) {
            stmt.setLong(1, dashboardId);
            stmt.setLong(2, watchlistItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting dashboard item", e);
        }
    }


    private static class DashboardItemSqlQueries {
        private static final String TABLE_NAME = "dashboard_items";

        private static final String COLUMN_DASHBOARD_ID = "dashboard_id";
        private static final String COLUMN_WATCHLIST_ITEM_ID = "watchlist_item_id";
        private static final String COLUMN_POSITION = "position";

        private static final String SQL_FIND_BY_DASHBOARD_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DASHBOARD_ID + " = ? ORDER BY " + COLUMN_POSITION;

        private static final String SQL_INSERT =
                "INSERT INTO " + TABLE_NAME + " (" +
                        COLUMN_DASHBOARD_ID + ", " +
                        COLUMN_WATCHLIST_ITEM_ID + ", " +
                        COLUMN_POSITION + ") VALUES (?, ?, ?)";

        private static final String SQL_UPDATE_POSITION =
                "UPDATE " + TABLE_NAME + " SET " + COLUMN_POSITION + " = ? " +
                        "WHERE " + COLUMN_DASHBOARD_ID + " = ? AND " + COLUMN_WATCHLIST_ITEM_ID + " = ?";

        private static final String SQL_DELETE =
                "DELETE FROM " + TABLE_NAME +
                        " WHERE " + COLUMN_DASHBOARD_ID + " = ? AND " + COLUMN_WATCHLIST_ITEM_ID + " = ?";
    }
}
