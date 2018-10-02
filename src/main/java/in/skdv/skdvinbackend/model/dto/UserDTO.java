package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.util.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UserDTO {

    @NotNull
    @NotEmpty
    private String username;

    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;

    private List<Role> roles;

    public UserDTO() {}

    public UserDTO(String username, String email, List<Role> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
