package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractUser;
import in.skdv.skdvinbackend.model.entity.Role;

import java.util.List;

public class UserDTO extends AbstractUser {

    public UserDTO() {}

    public UserDTO(String username, String email, List<Role> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
