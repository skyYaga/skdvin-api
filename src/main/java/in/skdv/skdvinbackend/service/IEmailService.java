package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.EmailType;
import jakarta.mail.MessagingException;


public interface IEmailService {

    void saveMailInOutbox(int appointmentId, EmailType emailType);

    void saveMailInOutbox(int appointmentId, EmailType emailType, Appointment appointment);

    void sendAppointmentVerification(Appointment appointment) throws MessagingException;

    void sendAppointmentConfirmation(Appointment appointment) throws MessagingException;

    void sendAppointmentUnconfirmedCancellation(Appointment appointment) throws MessagingException;

    void sendAppointmentUpdated(Appointment appointment) throws MessagingException;

    void sendAppointmentDeleted(Appointment appointment) throws MessagingException;

    void sendAppointmentReminder(Appointment appointment) throws MessagingException;
}
