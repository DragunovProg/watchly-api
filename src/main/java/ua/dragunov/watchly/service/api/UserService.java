package ua.dragunov.watchly.service.api;

import ua.dragunov.watchly.model.dto.*;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserResponse registerUser(UserCreationRequest request);

    Optional<UserResponse> findUserById(long userId);

    Optional<UserResponse> findUserByUsername(String username);

    Optional<UserResponse> findUserByEmail(String email);

    List<UserSummaryResponse> getAllUsers();

    UserResponse updateUserProfile(long userId, UserProfileUpdateRequest request);

    void changeUserPassword(long userId, UserPasswordChangeRequest request);

    void deleteUser(long userId);

    void assignRoleToUser(long userId, String roleName);

    void removeRoleFromUser(long userId, String roleName);

    List<RoleResponse> getRolesForUser(long userId); // RoleResponse из предыдущего обсуждения

    boolean userHasRole(long userId, String roleName);

    // --- Вспомогательные методы (опционально) ---

    /**
     * Проверяет, доступно ли указанное имя пользователя.
     * @param username Имя пользователя для проверки.
     * @return true, если доступно, иначе false.
     */
    // boolean isUsernameAvailable(String username);

    /**
     * Проверяет, доступен ли указанный email.
     * @param email Email для проверки.
     * @return true, если доступен, иначе false.
     */
    // boolean isEmailAvailable(String email);
}