package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.Appointment;

import java.util.List;

public interface IAppointmentService {

    Appointment saveAppointment(Appointment appointment);

    Appointment updateAppointment(Appointment appointment);

    Appointment findAppointment(int id);

    List<Appointment> findAppointments();
}
