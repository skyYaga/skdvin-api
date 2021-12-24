package in.skdv.skdvinbackend.service.impl;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.PageFilter;
import com.auth0.client.mgmt.filter.RolesFilter;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import com.auth0.json.mgmt.RolesPage;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import in.skdv.skdvinbackend.exception.AuthConnectionException;
import in.skdv.skdvinbackend.model.common.user.UserListResult;
import in.skdv.skdvinbackend.model.dto.RoleDTO;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Auth0UserService implements IUserService {

    private final ManagementAPI managementAPI;

    @Autowired
    public Auth0UserService(ManagementAPI managementAPI) {
        this.managementAPI = managementAPI;
    }

    @Override
    public UserListResult getUsers(int pageNumber, int amountPerPage) {
        try {
            UserListResult userListResult = new UserListResult();
            retrieveUsers(userListResult, pageNumber, amountPerPage);
            userListResult.getUsers().forEach(userDTO -> userDTO.setRoles(retrieveRoles(userDTO.getUserId())));
            return userListResult;
        } catch (Auth0Exception e) {
            log.error("Error retrieving users from auth0", e);
            throw new AuthConnectionException("Error retrieving users from auth0", e);
        }
    }

    @Override
    public void updateUser(UserDTO user) {
        List<RoleDTO> existingRoles = retrieveRoles(user.getUserId());
        List<RoleDTO> updatedRoles = user.getRoles();

        List<String> roleIdsToRemove = findRolesToRemove(existingRoles, updatedRoles);
        List<String> roleIdsToAdd = findRolesToAdd(existingRoles, updatedRoles);

        try {
            if (!roleIdsToAdd.isEmpty()) {
                managementAPI.users().addRoles(user.getUserId(), roleIdsToAdd).execute();
            }
            if (!roleIdsToRemove.isEmpty()) {
                managementAPI.users().removeRoles(user.getUserId(), roleIdsToRemove).execute();
            }
        } catch (Auth0Exception e) {
            log.error("Error updating roles from auth0", e);
            throw new AuthConnectionException("Error updating roles from auth0", e);
        }
    }

    @Override
    public List<RoleDTO> getRoles() {
        try {
            RolesPage list = managementAPI.roles().list(new RolesFilter()).execute();
            List<Role> roleList = list.getItems();

            List<RoleDTO> returnList = new ArrayList<>();
            roleList.forEach(role -> {
                RoleDTO roleDTO = new RoleDTO(role.getId(), role.getName());
                returnList.add(roleDTO);
            });

            return returnList;
        } catch (Auth0Exception e) {
            log.error("Error retrieving roles from auth0", e);
            throw new AuthConnectionException("Error retrieving roles from auth0", e);
        }
    }

    protected List<String> findRolesToAdd(List<RoleDTO> existingRoles, List<RoleDTO> updatedRoles) {
        List<RoleDTO> rolesToAdd = new ArrayList<>(updatedRoles);
        rolesToAdd.removeAll(existingRoles);
        return rolesToAdd.stream().map(RoleDTO::getId).collect(Collectors.toList());
    }

    protected List<String> findRolesToRemove(List<RoleDTO> existingRoles, List<RoleDTO> updatedRoles) {
        List<RoleDTO> rolesToRemove = new ArrayList<>(existingRoles);
        rolesToRemove.removeAll(updatedRoles);
        return rolesToRemove.stream().map(RoleDTO::getId).collect(Collectors.toList());
    }

    private void retrieveUsers(UserListResult returnList, int pageNumber, int amountPerPage) throws Auth0Exception {
        UserFilter userFilter = new UserFilter().withPage(pageNumber, amountPerPage).withTotals(true);
        UsersPage list = managementAPI.users().list(userFilter).execute();
        List<User> userList = list.getItems();

        returnList.setStart(list.getStart());
        returnList.setTotal(list.getTotal());
        userList.forEach(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getId());
            userDTO.setEmail(user.getEmail());
            returnList.getUsers().add(userDTO);
        });
    }

    private List<RoleDTO> retrieveRoles(String userId) {
        try {
            RolesPage rolesPage = managementAPI.users().listRoles(userId, new PageFilter()).execute();
            return rolesPage.getItems().stream().map(role -> new RoleDTO(role.getId(), role.getName())).collect(Collectors.toList());
        } catch (Auth0Exception e) {
            log.error("Error retrieving roles from auth0", e);
            throw new AuthConnectionException("Error retrieving roles from auth0", e);
        }
    }
}
