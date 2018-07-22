package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.Appointment;

import java.util.List;

public interface IAppointmentService {

    void saveAppointment(Appointment appointment);

    List<Appointment> findAppointments();
}
