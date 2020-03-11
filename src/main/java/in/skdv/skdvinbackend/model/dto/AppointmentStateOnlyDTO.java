package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.entity.AppointmentState;

import javax.validation.constraints.NotNull;

public class AppointmentStateOnlyDTO {

    @NotNull
    private AppointmentState state;

    public AppointmentState getState() {
        return state;
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }
}
