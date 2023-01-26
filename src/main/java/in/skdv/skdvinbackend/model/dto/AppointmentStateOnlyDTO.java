package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.entity.AppointmentState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AppointmentStateOnlyDTO {

    @NotNull
    private AppointmentState state;

}
