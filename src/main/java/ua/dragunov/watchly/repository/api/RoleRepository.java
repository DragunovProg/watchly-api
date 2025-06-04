package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.Actor;
import ua.dragunov.watchly.model.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findById(long roleId);
    Optional<Role> findByName(String roleName);
    List<Role> findAllByUserId(long userId);

    List<Role> findAll();

    Role save(Role role);


    void deleteById(long roleId);



}
