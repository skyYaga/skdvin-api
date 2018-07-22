package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.Appointment;
import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.ISequenceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

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
    public void saveAppointment(Appointment appointment) {
        appointment.setId(sequenceService.getNextSequence(APPOINTMENT_SEQUENCE));
        appointment.setCreatedOn(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> findAppointments() {
        return appointmentRepository.findAll();
    }
}
