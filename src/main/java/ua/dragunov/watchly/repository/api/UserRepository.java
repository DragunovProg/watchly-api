package ua.dragunov.watchly.repository.api;

import ua.dragunov.watchly.model.entity.User;

public interface UserRepository {

    void save(User user);

    User findById(long id);
    User findByUsername(String username);

    User findByEmail(String email);

    void update(User user);

    void deleteById(long id);
    void deleteByUsername(String username);
    void deleteByEmail(String email);



}
