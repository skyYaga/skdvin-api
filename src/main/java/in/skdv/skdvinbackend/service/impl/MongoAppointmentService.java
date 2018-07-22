package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.Appointment;
import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;

import java.time.LocalDateTime;

public class MongoAppointmentService implements IAppointmentService {

    private AppointmentRepository appointmentRepository;

    public MongoAppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void saveAppointment(Appointment appointment) {
        appointment.setCreatedOn(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }
}
