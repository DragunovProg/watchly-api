package ua.dragunov.watchly.model.entity;

import java.time.ZonedDateTime;
import java.util.Objects;

public class WatchlistItem {
    private Long id;
    private Status status;
    private long userId;
    private long mediaItemId;
    private ZonedDateTime addedAt;
    private byte rating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMediaItemId() {
        return mediaItemId;
    }

    public void setMediaItemId(long mediaItemId) {
        this.mediaItemId = mediaItemId;
    }

    public ZonedDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(ZonedDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public byte getRating() {
        return rating;
    }

    public void setRating(byte rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WatchlistItem that)) return false;
        return userId == that.userId && mediaItemId == that.mediaItemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, mediaItemId);
    }
}
