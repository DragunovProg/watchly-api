package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.Actor;
import ua.dragunov.watchly.repository.api.ActorRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcActorRepository implements ActorRepository {
    private final DataSource dataSource;

    public JdbcActorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Actor> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(ActorSqlQueries.SQL_FIND_BY_ID)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToActor(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding actor by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Actor> findAll() {
        List<Actor> actors = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(ActorSqlQueries.SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                actors.add(mapRowToActor(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all actors", e);
        }
        return actors;
    }

    @Override
    public Actor save(Actor actor) {
        if (actor.getId() == null) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(ActorSqlQueries.SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

                fillStatementWithActorData(ps, actor, false);
                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new DataAccessException("Inserting actor failed, no rows affected.");
                }

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        actor.setId(keys.getLong(1));
                    }
                }

            } catch (SQLException e) {
                throw new DataAccessException("Error inserting actor", e);
            }
        } else {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(ActorSqlQueries.SQL_UPDATE)) {

                fillStatementWithActorData(ps, actor, true);
                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new DataAccessException("Updating actor failed, no rows affected.");
                }

            } catch (SQLException e) {
                throw new DataAccessException("Error updating actor", e);
            }
        }

        return actor;
    }

    @Override
    public void deleteById(long id) {

    }

    @Override
    public List<Actor> findByMediaItemId(long mediaItemId) {
        List<Actor> actors = new ArrayList<>();
        final String sqlFindByMediaItemId = "SELECT a.* FROM actors a " +
                "JOIN media_actors ma ON a.id = ma.actor_id " +
                "WHERE ma.media_item_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFindByMediaItemId)) {

            ps.setLong(1, mediaItemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actors.add(mapRowToActor(rs));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding actors by media item ID: " + mediaItemId, e);
        }

        return actors;
    }

    private void fillStatementWithActorData(PreparedStatement ps, Actor actor, boolean includeIdAtEnd) throws SQLException {
        ps.setString(1, actor.getFirstName());
        ps.setString(2, actor.getLastName());
        ps.setObject(3, actor.getBirthday() != null ? Date.valueOf(actor.getBirthday()) : null);
        ps.setString(4, actor.getBiography());
        ps.setString(5, actor.getPhotoUrl());
        ps.setString(6, actor.getExternalId());
        ps.setLong(7, actor.getApiSourceId());

        if (includeIdAtEnd) {
            ps.setLong(8, actor.getId());
        }
    }

    private Actor mapRowToActor(ResultSet rs) throws SQLException {
        Actor actor = new Actor();
        actor.setId(rs.getLong(ActorSqlQueries.COLUMN_ID));
        actor.setFirstName(rs.getString(ActorSqlQueries.COLUMN_FIRST_NAME));
        actor.setLastName(rs.getString(ActorSqlQueries.COLUMN_LAST_NAME));

        Date date = rs.getDate(ActorSqlQueries.COLUMN_BIRTHDAY);
        if (date != null) {
            actor.setBirthday(date.toLocalDate());
        }

        actor.setBiography(rs.getString(ActorSqlQueries.COLUMN_BIOGRAPHY));
        actor.setPhotoUrl(rs.getString(ActorSqlQueries.COLUMN_PHOTO_URL));
        actor.setExternalId(rs.getString(ActorSqlQueries.COLUMN_EXTERNAL_ID));
        actor.setApiSourceId(rs.getLong(ActorSqlQueries.COLUMN_API_SOURCE_ID));

        return actor;
    }

    private static class ActorSqlQueries {
        private static final String TABLE_NAME = "actors";

        private static final String COLUMN_ID = "id";
        private static final String COLUMN_FIRST_NAME = "first_name";
        private static final String COLUMN_LAST_NAME = "last_name";
        private static final String COLUMN_BIRTHDAY = "birthday";
        private static final String COLUMN_BIOGRAPHY = "biography";
        private static final String COLUMN_PHOTO_URL = "photo_url";
        private static final String COLUMN_EXTERNAL_ID = "external_id";
        private static final String COLUMN_API_SOURCE_ID = "api_source_id";

        private static final String SQL_FIND_BY_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_FIND_ALL =
                "SELECT * FROM " + TABLE_NAME;

        private static final String SQL_INSERT =
                "INSERT INTO " + TABLE_NAME + " (" +
                        COLUMN_FIRST_NAME + ", " +
                        COLUMN_LAST_NAME + ", " +
                        COLUMN_BIRTHDAY + ", " +
                        COLUMN_BIOGRAPHY + ", " +
                        COLUMN_PHOTO_URL + ", " +
                        COLUMN_EXTERNAL_ID + ", " +
                        COLUMN_API_SOURCE_ID + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        private static final String SQL_UPDATE =
                "UPDATE " + TABLE_NAME + " SET " +
                        COLUMN_FIRST_NAME + " = ?, " +
                        COLUMN_LAST_NAME + " = ?, " +
                        COLUMN_BIRTHDAY + " = ?, " +
                        COLUMN_BIOGRAPHY + " = ?, " +
                        COLUMN_PHOTO_URL + " = ?, " +
                        COLUMN_EXTERNAL_ID + " = ?, " +
                        COLUMN_API_SOURCE_ID + " = ? " +
                        "WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_DELETE_BY_ID =
                "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

    }
}
