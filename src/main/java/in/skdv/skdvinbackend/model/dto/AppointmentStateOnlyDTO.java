package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.entity.AppointmentState;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AppointmentStateOnlyDTO {

    @NotNull
    private AppointmentState state;

}
