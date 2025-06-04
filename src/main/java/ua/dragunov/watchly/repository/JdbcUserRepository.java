package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.User;
import ua.dragunov.watchly.repository.api.UserRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private final DataSource dataSource;

    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(UserSqlQueries.SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPasswordHash());
                stmt.setString(4, user.getAvatarUrl());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException("Error saving user", e);
            }
        } else {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(UserSqlQueries.SQL_UPDATE)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPasswordHash());
                stmt.setString(4, user.getAvatarUrl());
                stmt.setLong(5, user.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error updating user with id: " + user.getId(), e);
            }
        }

        return user;
    }

    @Override
    public User findById(long id) {
        return findOneBy(UserSqlQueries.SQL_FIND_BY_ID, id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return findOneBy(UserSqlQueries.SQL_FIND_BY_USERNAME, username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return findOneBy(UserSqlQueries.SQL_FIND_BY_EMAIL, email).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        deleteBy(UserSqlQueries.SQL_DELETE_BY_ID, id);
    }

    @Override
    public void deleteByUsername(String username) {
        deleteBy(UserSqlQueries.SQL_DELETE_BY_USERNAME, username);
    }

    @Override
    public void deleteByEmail(String email) {
        deleteBy(UserSqlQueries.SQL_DELETE_BY_EMAIL, email);
    }

    private Optional<User> findOneBy(String sql, Object param) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error executing query: " + sql + " with param: " + param, e);
        }
        return Optional.empty();
    }

    private void deleteBy(String sql, Object param) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, param);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error executing delete query: " + sql + " with param: " + param, e);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong(UserSqlQueries.COLUMN_ID));
        user.setUsername(rs.getString(UserSqlQueries.COLUMN_USERNAME));
        user.setEmail(rs.getString(UserSqlQueries.COLUMN_EMAIL));
        user.setPasswordHash(rs.getString(UserSqlQueries.COLUMN_PASSWORD_HASH));
        user.setAvatarUrl(rs.getString(UserSqlQueries.COLUMN_AVATAR_URL));
        Timestamp ts = rs.getTimestamp(UserSqlQueries.COLUMN_CREATED_AT);
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }
        return user;
    }

    private static class UserSqlQueries {
        private static final String TABLE_NAME = "users";

        private static final String COLUMN_ID = "id";
        private static final String COLUMN_USERNAME = "username";
        private static final String COLUMN_EMAIL = "email";
        private static final String COLUMN_PASSWORD_HASH = "password_hash";
        private static final String COLUMN_AVATAR_URL = "avatar_url";
        private static final String COLUMN_CREATED_AT = "created_at";

        private static final String SQL_INSERT =
                "INSERT INTO " + TABLE_NAME + " (" +
                        COLUMN_USERNAME + ", " +
                        COLUMN_EMAIL + ", " +
                        COLUMN_PASSWORD_HASH + ", " +
                        COLUMN_AVATAR_URL + ") " +
                        "VALUES (?, ?, ?, ?)";

        private static final String SQL_FIND_BY_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_FIND_BY_USERNAME =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

        private static final String SQL_FIND_BY_EMAIL =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?";

        private static final String SQL_UPDATE =
                "UPDATE " + TABLE_NAME + " SET " +
                        COLUMN_USERNAME + " = ?, " +
                        COLUMN_EMAIL + " = ?, " +
                        COLUMN_PASSWORD_HASH + " = ?, " +
                        COLUMN_AVATAR_URL + " = ? " +
                        "WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_DELETE_BY_ID =
                "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_DELETE_BY_USERNAME =
                "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

        private static final String SQL_DELETE_BY_EMAIL =
                "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?";
    }
}
