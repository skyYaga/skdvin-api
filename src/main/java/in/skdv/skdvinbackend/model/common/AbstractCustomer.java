package in.skdv.skdvinbackend.model.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AbstractCustomer {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String tel;
    @NotNull
    private String zip;
    @NotNull
    private String city;

}
