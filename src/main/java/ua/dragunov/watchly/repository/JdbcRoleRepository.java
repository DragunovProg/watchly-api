package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.Role;
import ua.dragunov.watchly.repository.api.RoleRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRoleRepository implements RoleRepository {
    private final DataSource dataSource;

    public JdbcRoleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Role> findById(long roleId) {

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement(RoleSqlQueries.SQL_FIND_BY_ID)) {


            preparedStatement.setLong(1, roleId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Role());
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Role> findByName(String roleName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(RoleSqlQueries.SQL_FIND_BY_NAME)) {

            statement.setString(1, roleName);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRole(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding role by name: " + roleName, e);
        }
        return Optional.empty();

    }

    @Override
    public List<Role> findAllByUserId(long userId) {
        List<Role> roles = new ArrayList<>();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(RoleSqlQueries.SQL_FIND_ALL_BY_USER_ID)) {

            selectStatement.setLong(1, userId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    roles.add(mapRowToRole(resultSet));
                }
            }

            return roles;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding roles by user ID " + userId, e);
        }
    }

    @Override
    public List<Role> findAll() {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(RoleSqlQueries.SQL_FIND_ALL);
            ResultSet resultSet = statement.executeQuery();) {

            List<Role> roles = new ArrayList<>();

            while (resultSet.next()) {
                roles.add(mapRowToRole(resultSet));
            }

            return roles;

        } catch (SQLException e) {
            throw new DataAccessException("Error finding all roles", e);
        }
    }

    @Override
    public Role save(Role role) {
        if (role.getId() == null) {
            try(Connection connection = dataSource.getConnection();
                PreparedStatement insert = connection.prepareStatement(RoleSqlQueries.SQL_INSERT)) {

                insert.setString(1, role.getName());
                int affectedRows = insert.executeUpdate();

                if (affectedRows == 0) {
                    throw new DataAccessException("Error inserting new role " + role.getName());
                }

            } catch (SQLException e) {
                throw new DataAccessException(String.format("Error inserting role %s", role.getName()), e);
            }
        } else {
            try(Connection connection = dataSource.getConnection();
                PreparedStatement update = connection.prepareStatement(RoleSqlQueries.SQL_UPDATE)) {

                update.setString(1, role.getName());
                int affectedRows = update.executeUpdate();

                if (affectedRows == 0) {
                    throw new DataAccessException("Error inserting new role " + role.getName());
                }

            } catch (SQLException e) {
                throw new DataAccessException(String.format("Error inserting role %s", role.getName()), e);
            }
        }

        return role;
    }

    @Override
    public void deleteById(long roleId) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement delete = connection.prepareStatement(RoleSqlQueries.SQL_DELETE_BY_ID)) {

            delete.setLong(1, roleId);

            int affectedRows = delete.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Error inserting new role " + roleId);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting new role " + roleId, e);
        }
    }


    private Role mapRowToRole(ResultSet resultSet) throws SQLException {
        Role role = new Role();

        role.setId(resultSet.getLong(RoleSqlQueries.COLUMN_ID));
        role.setName(resultSet.getString(RoleSqlQueries.COLUMN_NAME));

        return role;
    }

    private static class RoleSqlQueries {
        private static final String TABLE_NAME = "roles";
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_NAME = "name";
        private static final String COLUMN_USER_ID = "user_id";
        private static final String TABLE_USER_ROLES = "user_roles";

        private static final String SQL_FIND_BY_ID = "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        private static final String SQL_FIND_BY_NAME = "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?";
        private static final String SQL_FIND_ALL = "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + " FROM " + TABLE_NAME;
        private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_NAME + ") VALUES (?)";
        private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = ? WHERE " + COLUMN_ID + " = ?";
        private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_FIND_ALL_BY_USER_ID = String.format("""
                SELECT r.%s, r.%s FROM %s r
                INNER JOIN %s ur ON r.%s = ur.%s
                WHERE ur.%s = ?
                """,COLUMN_ID, COLUMN_NAME, TABLE_NAME, TABLE_USER_ROLES, COLUMN_ID, COLUMN_USER_ID, COLUMN_ID);
    }
}
