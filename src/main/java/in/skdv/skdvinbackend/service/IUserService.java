package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.common.user.UserListResult;
import in.skdv.skdvinbackend.model.dto.RoleDTO;
import in.skdv.skdvinbackend.model.dto.UserDTO;

import java.util.List;

public interface IUserService {
    UserListResult getUsers(int pageNumber, int amountPerPage);

    void updateUser(UserDTO user);

    List<RoleDTO> getRoles();
}
