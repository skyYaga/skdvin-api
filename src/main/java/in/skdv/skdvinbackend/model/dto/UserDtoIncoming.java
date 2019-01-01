package in.skdv.skdvinbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.util.ValidEmail;
import in.skdv.skdvinbackend.util.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDtoIncoming {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    @ValidPassword
    private String password;

    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;

    private List<Role> roles;

    public UserDtoIncoming() {}

    public UserDtoIncoming(String username, String password, String email, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
