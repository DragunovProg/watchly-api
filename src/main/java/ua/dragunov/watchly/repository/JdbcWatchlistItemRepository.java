package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.Status;
import ua.dragunov.watchly.model.entity.WatchlistItem;
import ua.dragunov.watchly.repository.api.WatchlistItemRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWatchlistItemRepository implements WatchlistItemRepository {

    private final DataSource dataSource;

    public JdbcWatchlistItemRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<WatchlistItem> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistItemSqlQueries.SQL_FIND_BY_ID)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToWatchlistItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding watchlist item by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<WatchlistItem> findByUserId(long userId) {
        List<WatchlistItem> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistItemSqlQueries.SQL_FIND_BY_USER_ID)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToWatchlistItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding watchlist items by user ID: " + userId, e);
        }
        return items;
    }

    @Override
    public List<WatchlistItem> findByDashboard(long dashboardId) {
        List<WatchlistItem> items = new ArrayList<>();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(WatchlistItemSqlQueries.SQL_FIND_BY_DASHBOARD_ID)) {

            stmt.setLong(1, dashboardId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToWatchlistItem(rs));
                }
            }

            return items;

        } catch (SQLException e) {
            throw new DataAccessException("Error finding watchlist items by dashboard ID: " + dashboardId, e);
        }

    }

    @Override
    public WatchlistItem save(WatchlistItem item) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistItemSqlQueries.SQL_INSERT)) {

            stmt.setString(1, item.getStatus().name());
            stmt.setLong(2, item.getUserId());
            stmt.setLong(3, item.getMediaItemId());
            stmt.setByte(4, item.getRating());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    item.setId(rs.getLong(WatchlistItemSqlQueries.COLUMN_ID));
                    item.setAddedAt(rs.getTimestamp(WatchlistItemSqlQueries.COLUMN_ADDED_AT).toInstant().atZone(ZoneId.systemDefault()));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error saving watchlist item", e);
        }
        return item;
    }

    @Override
    public void update(WatchlistItem item) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistItemSqlQueries.SQL_UPDATE)) {

            stmt.setString(1, item.getStatus().name());
            stmt.setLong(2, item.getUserId());
            stmt.setLong(3, item.getMediaItemId());
            stmt.setByte(4, item.getRating());
            stmt.setLong(5, item.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error updating watchlist item with id: " + item.getId(), e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistItemSqlQueries.SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting watchlist item with id: " + id, e);
        }
    }

    private WatchlistItem mapRowToWatchlistItem(ResultSet rs) throws SQLException {
        WatchlistItem item = new WatchlistItem();
        item.setId(rs.getLong(WatchlistItemSqlQueries.COLUMN_ID));
        item.setStatus(Status.valueOf(rs.getString(WatchlistItemSqlQueries.COLUMN_STATUS)));
        item.setUserId(rs.getLong(WatchlistItemSqlQueries.COLUMN_USER_ID));
        item.setMediaItemId(rs.getLong(WatchlistItemSqlQueries.COLUMN_MEDIA_ITEM_ID));
        item.setRating(rs.getByte(WatchlistItemSqlQueries.COLUMN_RATING));
        Timestamp ts = rs.getTimestamp(WatchlistItemSqlQueries.COLUMN_ADDED_AT);
        if (ts != null) {
            item.setAddedAt(ts.toInstant().atZone(ZoneId.systemDefault()));
        }
        return item;
    }

    private static class WatchlistItemSqlQueries {
        private static final String TABLE_NAME = "watchlist_items";

        private static final String COLUMN_ID = "id";
        private static final String COLUMN_STATUS = "status";
        private static final String COLUMN_USER_ID = "user_id";
        private static final String COLUMN_MEDIA_ITEM_ID = "media_item_id";
        private static final String COLUMN_ADDED_AT = "added_at";
        private static final String COLUMN_RATING = "rating";

        private static final String SQL_FIND_BY_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_FIND_BY_USER_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = ?";

        private static final String SQL_INSERT =
                "INSERT INTO " + TABLE_NAME + " (" +
                        COLUMN_STATUS + ", " +
                        COLUMN_USER_ID + ", " +
                        COLUMN_MEDIA_ITEM_ID + ", " +
                        COLUMN_RATING + ") " +
                        "VALUES (?, ?, ?, ?) RETURNING " + COLUMN_ID + ", " + COLUMN_ADDED_AT;

        private static final String SQL_UPDATE =
                "UPDATE " + TABLE_NAME + " SET " +
                        COLUMN_STATUS + " = ?, " +
                        COLUMN_USER_ID + " = ?, " +
                        COLUMN_MEDIA_ITEM_ID + " = ?, " +
                        COLUMN_RATING + " = ? " +
                        "WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_DELETE =
                "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_FIND_BY_DASHBOARD_ID =
                "SELECT wi.* FROM watchlist_item wi " +
                        "JOIN dashboard_items di ON wi.id = di.watchlist_item_id " +
                        "WHERE di.dashboard_id = ?";
    }
}