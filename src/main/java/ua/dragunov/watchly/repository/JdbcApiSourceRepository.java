package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.ApiSource;
import ua.dragunov.watchly.repository.api.ApiSourceRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcApiSourceRepository implements ApiSourceRepository {
    private final DataSource dataSource;


    public JdbcApiSourceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<ApiSource> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ApiSourceSqlQueries.SQL_FIND_BY_ID)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToApiSource(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding ApiSource by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ApiSource> findByName(String name) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ApiSourceSqlQueries.SQL_FIND_BY_NAME)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToApiSource(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding ApiSource by name: " + name, e);
        }
        return Optional.empty();
    }

    @Override
    public List<ApiSource> findAll() {
        List<ApiSource> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ApiSourceSqlQueries.SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToApiSource(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all ApiSources", e);
        }
        return result;
    }

    @Override
    public ApiSource save(ApiSource source) {
        if (source.getId() == null) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(ApiSourceSqlQueries.SQL_INSERT)) {

                stmt.setString(1, source.getName());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        source.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException("Error inserting ApiSource", e);
            }
        } else {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(ApiSourceSqlQueries.SQL_UPDATE)) {

                stmt.setString(1, source.getName());
                stmt.setLong(2, source.getId());

                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error updating ApiSource", e);
            }
        }
        return source;
    }

    public static ApiSource mapRowToApiSource(ResultSet rs) throws SQLException {
        ApiSource apiSource = new ApiSource();
        apiSource.setId(rs.getLong("id"));
        apiSource.setName(rs.getString("name"));
        return apiSource;
    }

    private static class ApiSourceSqlQueries {
        private static final String TABLE_NAME = "api_source";
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_NAME = "name";

        private static final String SQL_FIND_BY_ID = "SELECT " + COLUMN_ID + "," + COLUMN_NAME + " FROM "+ TABLE_NAME +" WHERE id = ?";
        private static final String SQL_FIND_BY_NAME = "SELECT " + COLUMN_ID + "," + COLUMN_NAME + " FROM " + TABLE_NAME + " WHERE "+ COLUMN_NAME + " = ?";
        private static final String SQL_FIND_ALL = "SELECT " + COLUMN_ID + "," + COLUMN_NAME + " FROM " + TABLE_NAME;
        private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " ("+ COLUMN_NAME + ") VALUES (?) RETURNING " + COLUMN_ID;

        private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = ? WHERE " + COLUMN_ID + " = ?";
    }
}
