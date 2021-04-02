package in.skdv.skdvinbackend.model.common.waiver;

import in.skdv.skdvinbackend.model.common.AbstractCustomer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
public class WaiverCustomer extends AbstractCustomer {

    @NotNull
    private String street;

    @Past
    @NotNull
    private LocalDate dateOfBirth;
}
