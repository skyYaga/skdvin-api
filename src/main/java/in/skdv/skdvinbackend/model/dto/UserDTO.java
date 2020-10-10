package in.skdv.skdvinbackend.model.dto;

import java.util.List;

public class UserDTO {

    private String userId;
    private String email;
    private List<String> roles;

    public UserDTO() {}

    public UserDTO(String userId, String email, List<String> roles) {
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
