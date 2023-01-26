package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractCustomer;
import in.skdv.skdvinbackend.util.ValidEmail;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends AbstractCustomer {

    @NotNull
    @ValidEmail
    private String email;
    @NotNull
    private List<Jumper> jumpers;

}
