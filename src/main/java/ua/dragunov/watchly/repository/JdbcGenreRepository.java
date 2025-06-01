package ua.dragunov.watchly.repository;

import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.Genre;
import ua.dragunov.watchly.repository.api.GenreRepository;


import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGenreRepository implements GenreRepository {

    private final DataSource dataSource;



    public JdbcGenreRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Genre> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GenreSqlQueries.SQL_FIND_BY_ID)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGenre(rs));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding Genre by id: " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public Genre save(Genre genre) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GenreSqlQueries.SQL_INSERT)) {

            stmt.setString(1, genre.getName());
            stmt.setString(2, genre.getExternalId());
            stmt.setLong(3, genre.getApiSourceId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    genre.setId(rs.getLong(GenreSqlQueries.COLUMN_ID));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error saving Genre", e);
        }

        return genre;
    }

    @Override
    public List<Genre> findAll() {
        List<Genre> genres = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GenreSqlQueries.SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                genres.add(mapRowToGenre(rs));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all Genres", e);
        }
        return genres;
    }

    @Override
    public List<Genre> findByMediaItemId(long mediaItemId) {
        List<Genre> genres = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GenreSqlQueries.SQL_FIND_BY_MEDIA_ITEM_ID)) {

            stmt.setLong(1, mediaItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(mapRowToGenre(rs));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding Genres by media item ID: " + mediaItemId, e);
        }
        return genres;
    }

    public static Genre mapRowToGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));
        genre.setExternalId(rs.getString("external_id"));
        genre.setApiSourceId(rs.getLong("api_source_id"));
        return genre;
    }

    private static class GenreSqlQueries {
        private static final String TABLE_NAME = "genres";

        private static final String COLUMN_ID = "id";
        private static final String COLUMN_NAME = "name";
        private static final String COLUMN_EXTERNAL_ID = "external_id";
        private static final String COLUMN_API_SOURCE_ID = "api_source_id";

        private static final String SQL_FIND_BY_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_INSERT =
                "INSERT INTO " + TABLE_NAME + " (" +
                        COLUMN_NAME + ", " +
                        COLUMN_EXTERNAL_ID + ", " +
                        COLUMN_API_SOURCE_ID + ") VALUES (?, ?, ?) RETURNING id";

        private static final String SQL_FIND_ALL =
                "SELECT * FROM " + TABLE_NAME;

        private static final String SQL_FIND_BY_MEDIA_ITEM_ID =
                "SELECT g.* FROM genres g " +
                        "JOIN media_genres mg ON g.id = mg.genre_id " +
                        "WHERE mg.media_item_id = ?";
    }
}
