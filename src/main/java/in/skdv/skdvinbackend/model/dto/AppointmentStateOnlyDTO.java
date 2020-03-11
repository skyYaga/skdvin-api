package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.entity.AppointmentState;

public class AppointmentStateOnlyDTO {

    private AppointmentState state;

    public AppointmentState getState() {
        return state;
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }
}
