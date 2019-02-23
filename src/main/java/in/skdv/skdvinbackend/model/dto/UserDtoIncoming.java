package in.skdv.skdvinbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.skdv.skdvinbackend.model.common.AbstractUser;
import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.util.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDtoIncoming extends AbstractUser {

    @NotNull
    @NotEmpty
    @ValidPassword
    private String password;

    public UserDtoIncoming() {}

    public UserDtoIncoming(String username, String password, String email, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
