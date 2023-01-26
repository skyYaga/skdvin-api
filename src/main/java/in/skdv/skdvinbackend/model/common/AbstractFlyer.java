package in.skdv.skdvinbackend.model.common;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public abstract class AbstractFlyer {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String email;
    private String tel;
    private boolean favorite;

}
