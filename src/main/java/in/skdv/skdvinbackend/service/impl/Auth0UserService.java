package in.skdv.skdvinbackend.service.impl;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.PageFilter;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import com.auth0.json.mgmt.RolesPage;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import in.skdv.skdvinbackend.exception.AuthConnectionException;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Auth0UserService implements IUserService {

    private static final Logger LOG = LoggerFactory.getLogger(Auth0UserService.class);

    private final ManagementAPI managementAPI;

    @Autowired
    public Auth0UserService(ManagementAPI managementAPI) {
        this.managementAPI = managementAPI;
    }

    @Override
    public List<UserDTO> getUsers() {
        try {
            List<UserDTO> returnList = new ArrayList<>();
            retrieveUsers(returnList);
            returnList.forEach(this::retrieveRoles);
            return returnList;
        } catch (Auth0Exception e) {
            throw new AuthConnectionException("Error retrieving users from auth0", e);
        }
    }

    private void retrieveUsers(List<UserDTO> returnList) throws Auth0Exception {
        UsersPage list = managementAPI.users().list(new UserFilter()).execute();
        List<User> userList = list.getItems();

        userList.forEach(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getId());
            userDTO.setEmail(user.getEmail());
            returnList.add(userDTO);
        });
    }

    private void retrieveRoles(UserDTO user) {
        try {
            RolesPage rolesPage = managementAPI.users().listRoles(user.getUserId(), new PageFilter()).execute();
            user.setRoles(rolesPage.getItems().stream().map(Role::getName).collect(Collectors.toList()));
        } catch (Auth0Exception e) {
            throw new AuthConnectionException("Error retrieving roles from auth0", e);
        }
    }
}
