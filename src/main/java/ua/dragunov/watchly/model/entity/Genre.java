package ua.dragunov.watchly.model.entity;

import java.util.Objects;

public class Genre {
    private Long id;
    private String name;
    private String externalId;
    private long apiSourceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public long getApiSourceId() {
        return apiSourceId;
    }

    public void setApiSourceId(long apiSourceId) {
        this.apiSourceId = apiSourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre genre)) return false;
        return Objects.equals(name, genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
