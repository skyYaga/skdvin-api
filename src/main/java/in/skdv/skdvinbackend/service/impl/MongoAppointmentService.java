package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.ISequenceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MongoAppointmentService implements IAppointmentService {

    private static final String APPOINTMENT_SEQUENCE = "appointment";

    private AppointmentRepository appointmentRepository;
    private ISequenceService sequenceService;

    @Autowired
    public MongoAppointmentService(AppointmentRepository appointmentRepository, ISequenceService sequenceService) {
        this.appointmentRepository = appointmentRepository;
        this.sequenceService = sequenceService;
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        appointment.setAppointmentId(sequenceService.getNextSequence(APPOINTMENT_SEQUENCE));
        appointment.setCreatedOn(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment updateAppointment(Appointment appointment) {
        appointment.setCreatedOn(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment findAppointment(int id) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        return appointmentOptional.orElse(null);
    }

    @Override
    public List<Appointment> findAppointments() {
        return appointmentRepository.findAll();
    }
}
