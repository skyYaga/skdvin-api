package in.skdv.skdvinbackend.model.common;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
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
