package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentService {

    GenericResult<Appointment> saveAppointment(Appointment appointment);

    GenericResult<Appointment> updateAppointment(Appointment appointment);

    Appointment findAppointment(int id);

    List<Appointment> findAppointmentsByDay(LocalDate date);
}
