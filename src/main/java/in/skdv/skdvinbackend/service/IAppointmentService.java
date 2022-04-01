package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.GroupSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentService {

    Appointment saveAppointment(Appointment appointment);

    Appointment saveAdminAppointment(Appointment appointment);

    Appointment updateAppointment(Appointment appointment);

    Appointment updateAdminAppointment(Appointment appointment);

    Appointment findAppointment(int id);

    List<Appointment> findAppointmentsByDay(LocalDate date);

    List<FreeSlot> findFreeSlots(SlotQuery slotQuery);

    void updateAppointmentState(Appointment appointment, AppointmentState appointmentState);

    List<Appointment> findUnconfirmedAppointments();

    void deleteAppointment(Appointment appointment);

    void deleteAppointment(int appointmentId);

    List<GroupSlot> findGroupSlots(SlotQuery slotQuery);

    List<Appointment> findAppointmentsWithinNextWeek();

    void reminderSent(Appointment appointment);

    void confirmAppointment(int appointmentId, String token);
}
