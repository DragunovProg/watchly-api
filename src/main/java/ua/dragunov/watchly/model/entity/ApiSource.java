package ua.dragunov.watchly.model.entity;

import java.util.Objects;

public class ApiSource {
    private Long id;
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiSource apiSource)) return false;
        return Objects.equals(name, apiSource.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
