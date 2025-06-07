package ua.dragunov.watchly.service.api;

import ua.dragunov.watchly.model.dto.RoleCreationRequest;
import ua.dragunov.watchly.model.dto.RoleResponse;
import ua.dragunov.watchly.model.dto.RoleUpdateRequest;
import ua.dragunov.watchly.model.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    RoleResponse createRole(RoleCreationRequest request);

    Optional<RoleResponse> findRoleById(long roleId);

    Optional<RoleResponse> findRoleByName(String roleName);

    List<RoleResponse> getAllRoles();

    RoleResponse updateRoleName(long roleId, RoleUpdateRequest request);

    void deleteRole(long roleId);

    RoleResponse getOrCreateRole(String roleName);
}