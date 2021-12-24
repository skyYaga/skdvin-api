package in.skdv.skdvinbackend.model.common;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public abstract class AbstractFlyer {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String email;
    private String tel;

}
