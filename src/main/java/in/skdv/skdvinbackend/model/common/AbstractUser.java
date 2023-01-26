package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.util.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
