package ua.dragunov.watchly.repository;



import ua.dragunov.watchly.exceptions.DataAccessException;
import ua.dragunov.watchly.model.entity.MediaItem;
import ua.dragunov.watchly.repository.api.MediaItemRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMediaItemRepository implements MediaItemRepository {

    private final DataSource dataSource;



    public JdbcMediaItemRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<MediaItem> findById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MediaItemSqlQueries.SQL_FIND_BY_ID)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToMediaItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding MediaItem by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<MediaItem> findAll() {
        List<MediaItem> items = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MediaItemSqlQueries.SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapRowToMediaItem(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all MediaItems", e);
        }
        return items;
    }

    @Override
    public MediaItem save(MediaItem item) {
        if (item.getId() == null) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(MediaItemSqlQueries.SQL_INSERT)) {

                fillStatementProperties(item, stmt);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        item.setId(rs.getLong(MediaItemSqlQueries.COLUMN_ID));
                        Timestamp ts = rs.getTimestamp(MediaItemSqlQueries.COLUMN_CREATED_AT);
                        if (ts != null) {
                            item.setCreatedAt(ts.toInstant().atZone(ZoneId.systemDefault()));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException("Error saving MediaItem", e);
            }
        } else {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(MediaItemSqlQueries.SQL_UPDATE)) {
                fillStatementProperties(item, stmt);
                stmt.setLong(7, item.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error updating MediaItem with id: " + item.getId(), e);
            }
        }

        return item;
    }


    @Override
    public void delete(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MediaItemSqlQueries.SQL_DELETE)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting MediaItem with id: " + id, e);
        }
    }

    private void fillStatementProperties(MediaItem item, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, item.getTitle());
        stmt.setString(2, item.getDescription());
        stmt.setInt(3, item.getReleaseYear());
        stmt.setString(4, item.getPictureUrl());
        stmt.setString(5, item.getExternalId());
        stmt.setLong(6, item.getApiSourceId());
    }

    public static MediaItem mapRowToMediaItem(ResultSet rs) throws SQLException {
        MediaItem mediaItem = new MediaItem();
        mediaItem.setId(rs.getLong("id"));
        mediaItem.setTitle(rs.getString("title"));
        mediaItem.setDescription(rs.getString("description"));
        mediaItem.setReleaseYear(rs.getInt("release_year"));
        mediaItem.setPictureUrl(rs.getString("picture_url"));
        mediaItem.setExternalId(rs.getString("external_id"));

        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            mediaItem.setCreatedAt(ZonedDateTime.ofInstant(createdAtTimestamp.toInstant(), ZoneId.systemDefault()));
        } else {
            mediaItem.setCreatedAt(null);
        }

        mediaItem.setApiSourceId(rs.getLong("api_source_id"));
        return mediaItem;
    }

    private static class MediaItemSqlQueries {
        private static final String TABLE_NAME = "media_item";

        private static final String COLUMN_ID = "id";
        private static final String COLUMN_TITLE = "title";
        private static final String COLUMN_DESCRIPTION = "description";
        private static final String COLUMN_RELEASE_YEAR = "release_year";
        private static final String COLUMN_PICTURE_URL = "picture_url";
        private static final String COLUMN_EXTERNAL_ID = "external_id";
        private static final String COLUMN_CREATED_AT = "created_at";
        private static final String COLUMN_API_SOURCE_ID = "api_source_id";

        private static final String SQL_FIND_BY_ID =
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_FIND_ALL =
                "SELECT * FROM " + TABLE_NAME;

        private static final String SQL_INSERT =
                "INSERT INTO " + TABLE_NAME + " (" +
                        COLUMN_TITLE + ", " +
                        COLUMN_DESCRIPTION + ", " +
                        COLUMN_RELEASE_YEAR + ", " +
                        COLUMN_PICTURE_URL + ", " +
                        COLUMN_EXTERNAL_ID + ", " +
                        COLUMN_API_SOURCE_ID + ") " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING " + COLUMN_ID + ", " + COLUMN_CREATED_AT;

        private static final String SQL_UPDATE =
                "UPDATE " + TABLE_NAME + " SET " +
                        COLUMN_TITLE + " = ?, " +
                        COLUMN_DESCRIPTION + " = ?, " +
                        COLUMN_RELEASE_YEAR + " = ?, " +
                        COLUMN_PICTURE_URL + " = ?, " +
                        COLUMN_EXTERNAL_ID + " = ?, " +
                        COLUMN_API_SOURCE_ID + " = ? " +
                        "WHERE " + COLUMN_ID + " = ?";

        private static final String SQL_DELETE =
                "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    }
}
