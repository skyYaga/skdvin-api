package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.util.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public abstract class AbstractUser {

    @NotNull
    @NotEmpty
    protected String username;

    @ValidEmail
    @NotNull
    @NotEmpty
    protected String email;

    protected List<Role> roles;
}
