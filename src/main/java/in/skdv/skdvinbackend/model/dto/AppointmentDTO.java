package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractAppointment;

public class AppointmentDTO extends AbstractAppointment {

    private int appointmentId;

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

}
