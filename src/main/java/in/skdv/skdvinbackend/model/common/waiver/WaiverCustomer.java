package in.skdv.skdvinbackend.model.common.waiver;

import in.skdv.skdvinbackend.model.common.AbstractCustomer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class WaiverCustomer extends AbstractCustomer {

    @NotNull
    private String street;

    @Past
    @NotNull
    private LocalDate dateOfBirth;
}
