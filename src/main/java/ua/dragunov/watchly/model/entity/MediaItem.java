package ua.dragunov.watchly.model.entity;

import java.time.ZonedDateTime;
import java.util.Objects;

public class MediaItem {
    private Long id;
    private String title;
    private String description;
    private int releaseYear;
    private String pictureUrl;
    private String externalId;
    private ZonedDateTime createdAt;
    private long apiSourceId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getApiSourceId() {
        return apiSourceId;
    }

    public void setApiSourceId(long apiSourceId) {
        this.apiSourceId = apiSourceId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaItem mediaItem)) return false;
        return Objects.equals(title, mediaItem.title) && Objects.equals(description, mediaItem.description) && Objects.equals(releaseYear, mediaItem.releaseYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, releaseYear);
    }


}
