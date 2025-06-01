package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.WatchlistDashboard;
import ua.dragunov.watchly.repository.api.WatchlistDashboardRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWatchlistDashboardRepository implements WatchlistDashboardRepository {

    private final DataSource dataSource;

    public JdbcWatchlistDashboardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<WatchlistDashboard> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistDashboardSqlQueries.SQL_FIND_BY_ID)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDashboard(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding dashboard by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<WatchlistDashboard> findByUserId(long userId) {
        List<WatchlistDashboard> dashboards = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistDashboardSqlQueries.SQL_FIND_BY_USER_ID)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dashboards.add(mapRowToDashboard(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding dashboards by user id: " + userId, e);
        }
        return dashboards;
    }

    @Override
    public WatchlistDashboard save(WatchlistDashboard dashboard) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistDashboardSqlQueries.SQL_INSERT)) {
            stmt.setString(1, dashboard.getName());
            stmt.setLong(2, dashboard.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dashboard.setId(rs.getLong(WatchlistDashboardSqlQueries.COLUMN_ID));
                    Timestamp ts = rs.getTimestamp(WatchlistDashboardSqlQueries.COLUMN_CREATED_AT);
                    if (ts != null) {
                        dashboard.setCreatedAt(ts.toInstant().atZone(ZoneId.systemDefault()));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error saving dashboard", e);
        }
        return dashboard;
    }

    @Override
    public void delete(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WatchlistDashboardSqlQueries.SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting dashboard with id: " + id, e);
        }
    }

    private WatchlistDashboard mapRowToDashboard(ResultSet rs) throws SQLException {
        WatchlistDashboard dashboard = new WatchlistDashboard();
        dashboard.setId(rs.getLong(WatchlistDashboardSqlQueries.COLUMN_ID));
        dashboard.setName(rs.getString(WatchlistDashboardSqlQueries.COLUMN_NAME));
        dashboard.setUserId(rs.getLong(WatchlistDashboardSqlQueries.COLUMN_USER_ID));
        Timestamp ts = rs.getTimestamp(WatchlistDashboardSqlQueries.COLUMN_CREATED_AT);
        if (ts != null) {
            dashboard.setCreatedAt(ts.toInstant().atZone(ZoneId.systemDefault()));
        }
        return dashboard;
    }

    private static class WatchlistDashboardSqlQueries {
        private static final String TABLE_NAME = "watchlist_dashboards";

        private static final String COLUMN_ID = "id";
        private static final String COLUMN_NAME = "name";
        private static final String COLUMN_USER_ID = "user_id";
        private static final String COLUMN_CREATED_AT = "created_at";

        private static final String SQL_FIND_BY_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_FIND_BY_USER_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = ?";

        private static final String SQL_INSERT =
                "INSERT INTO " + TABLE_NAME + " (" +
                        COLUMN_NAME + ", " + COLUMN_USER_ID + ") " +
                        "VALUES (?, ?) RETURNING " + COLUMN_ID + ", " + COLUMN_CREATED_AT;

        private static final String SQL_DELETE =
                "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    }
}