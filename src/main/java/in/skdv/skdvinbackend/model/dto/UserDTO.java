package in.skdv.skdvinbackend.model.dto;

import java.util.List;

public class UserDTO {

    private String userId;
    private String email;
    private List<RoleDTO> roles;

    public UserDTO() {}

    public UserDTO(String userId, String email, List<RoleDTO> roles) {
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
