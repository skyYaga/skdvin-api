package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.util.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public abstract class AbstractUser {

    @NotNull
    @NotEmpty
    protected String username;

    @ValidEmail
    @NotNull
    @NotEmpty
    protected String email;

    protected List<Role> roles;


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

    @Override
    public String toString() {
        return "AbstractUser{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
