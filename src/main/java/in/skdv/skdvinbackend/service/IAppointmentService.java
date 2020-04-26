package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.GroupSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentService {

    GenericResult<Appointment> saveAppointment(Appointment appointment);

    GenericResult<Appointment> saveAdminAppointment(Appointment appointment);

    GenericResult<Appointment> updateAppointment(Appointment appointment);

    GenericResult<Appointment> updateAdminAppointment(Appointment appointment);

    Appointment findAppointment(int id);

    List<Appointment> findAppointmentsByDay(LocalDate date);

    GenericResult<List<FreeSlot>> findFreeSlots(SlotQuery slotQuery);

    GenericResult<Void> updateAppointmentState(Appointment appointment, AppointmentState appointmentState);

    List<Appointment> findUnconfirmedAppointments();

    void deleteAppointment(int appointmentId);

    List<GroupSlot> findGroupSlots(SlotQuery slotQuery);
}
