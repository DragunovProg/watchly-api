package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findById(long roleId);
    Optional<Role> findByName(String roleName);

    List<Role> findAll();

    void insert(Role role);

    void update(Role role);

    void deleteById(long roleId);



}
