package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.*;
import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.GroupSlot;
import in.skdv.skdvinbackend.model.common.SimpleSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Slot;
import in.skdv.skdvinbackend.repository.ISequenceRepository;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private static final String APPOINTMENT_SEQUENCE = "appointment";

    private final ZoneId zoneId;
    private final JumpdayRepository jumpdayRepository;
    private final ISequenceRepository sequenceService;
    private final Clock clock = Clock.systemDefaultZone();

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        Jumpday jumpday = jumpdayRepository.findByDate(appointment.getDate().atZone(zoneId).toLocalDate());
        return saveAppointmentInternal(jumpday, appointment, false);
    }

    @Override
    public Appointment saveAdminAppointment(Appointment appointment) {
        Jumpday jumpday = jumpdayRepository.findByDate(appointment.getDate().atZone(zoneId).toLocalDate());
        return saveAppointmentInternal(jumpday, appointment, true);
    }

    @Override
    public Appointment updateAppointment(Appointment newAppointment) {
        return updateAppointment(newAppointment, false);
    }

    @Override
    public Appointment updateAdminAppointment(Appointment appointment) {
        return updateAppointment(appointment, true);
    }


    private Appointment updateAppointment(Appointment newAppointment, boolean isAdminBooking) {
        Appointment oldAppointment = findAppointment(newAppointment.getAppointmentId());

        if (oldAppointment == null) {
            log.error("Appointment {} not found", newAppointment.getAppointmentId());
            throw new NotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND);
        }

        if (isAtSameDateAndTime(newAppointment, oldAppointment)) {
            // Delete Appointment in this jumpday and create a new one, then save
            return replaceAppointment(newAppointment, isAdminBooking);
        }

        // Create new Appointment and save it and then delete old one and save
        Appointment appointmentResult;
        if (isAdminBooking) {
            appointmentResult = saveAdminAppointment(newAppointment);
        } else {
            appointmentResult = saveAppointment(newAppointment);
        }
        deleteOldAppointment(newAppointment, oldAppointment);
        return appointmentResult;
    }

    private void deleteOldAppointment(Appointment newAppointment, Appointment oldAppointment) {
        Jumpday jumpday = jumpdayRepository.findByDate(oldAppointment.getDate().atZone(zoneId).toLocalDate());
        for (Slot slot : jumpday.getSlots()) {

            for (Iterator<Appointment> iterator = slot.getAppointments().iterator(); iterator.hasNext(); ) {
                Appointment appointment = iterator.next();

                if (appointment != null && appointment.getAppointmentId() == newAppointment.getAppointmentId()) {
                    iterator.remove();
                    jumpdayRepository.save(jumpday);
                }
            }
        }
    }

    private Appointment replaceAppointment(Appointment newAppointment, boolean isAdminBooking) {
        Jumpday jumpday = jumpdayRepository.findByDate(newAppointment.getDate().atZone(zoneId).toLocalDate());
        for (Slot slot : jumpday.getSlots()) {
            slot.getAppointments().removeIf(appointment -> appointment != null
                    && appointment.getAppointmentId() == newAppointment.getAppointmentId());
        }

        return saveAppointmentInternal(jumpday, newAppointment, isAdminBooking);
    }

    private boolean isAtSameDateAndTime(Appointment newAppointment, Appointment oldAppointment) {
        return oldAppointment.getDate().atZone(zoneId).toLocalDate().equals(newAppointment.getDate().atZone(zoneId).toLocalDate());
    }

    @Override
    public Appointment findAppointment(int id) {
        List<Jumpday> jumpdayList = jumpdayRepository.findBySlotsAppointmentsAppointmentId(id);

        Optional<Appointment> appointment = jumpdayList.stream()
                .flatMap(day -> day.getSlots().stream())
                .flatMap(s -> s.getAppointments().stream())
                .filter(a -> a != null && a.getAppointmentId() == id).findFirst();

        if (appointment.isEmpty()) {
            log.error("Appointment {} not found", id);
            throw new NotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND);
        }

        return appointment.get();
    }

    @Override
    public List<Appointment> findAppointmentsByDay(LocalDate date) {
        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday != null && jumpday.getSlots() != null) {
            return jumpday.getSlots().stream()
                    .flatMap(s -> s.getAppointments().stream().filter(Objects::nonNull)).toList();
        }
        return new ArrayList<>();
    }

    @Override
    public List<FreeSlot> findFreeSlots(SlotQuery slotQuery) {
        if (!slotQuery.isValid()) {
            log.error("Slot Query appointment has more video than tandem slots");
            throw new InvalidRequestException(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS);
        }

        List<Jumpday> jumpdayList = jumpdayRepository.findAllAfterIncludingDate(LocalDate.now());
        List<FreeSlot> resultList = new ArrayList<>();

        jumpdayList.forEach(jumpday -> {
            List<LocalTime> slotTimes = jumpday.getSlots().stream()
                    .filter(slot ->
                            (isTodayButTimeInFuture(jumpday, slot) || isInFuture(jumpday))
                                    && slot.isValidForQuery(slotQuery)
                    )
                    .map(Slot::getTime)
                    .toList();

            if (!slotTimes.isEmpty()) {
                resultList.add(new FreeSlot(jumpday.getDate(), slotTimes));
            }
        });

        return resultList;
    }

    @Override
    public void updateAppointmentState(Appointment appointment, AppointmentState appointmentState) {
        appointment.setState(appointmentState);
        updateAppointment(appointment);
    }

    @Override
    public List<Appointment> findUnconfirmedAppointments() {
        List<Jumpday> jumpdayList = jumpdayRepository.findAllAfterIncludingDate(LocalDate.now());

        return jumpdayList.stream()
                .flatMap(day -> day.getSlots().stream())
                .flatMap(s -> s.getAppointments().stream())
                .filter(a -> a != null && a.getVerificationToken() != null
                        && a.getVerificationToken().getExpiryDate().isBefore(LocalDateTime.now())
                        && a.getState().equals(AppointmentState.UNCONFIRMED))
                .toList();
    }

    @Override
    public void deleteAppointment(int appointmentId) {
        List<Jumpday> jumpdays = jumpdayRepository.findBySlotsAppointmentsAppointmentId(appointmentId);

        jumpdays.forEach(jumpday -> jumpday.getSlots().forEach(slot ->
                slot.getAppointments().removeIf(appointment -> appointment.getAppointmentId() == appointmentId)
        ));

        jumpdayRepository.saveAll(jumpdays);
    }

    @Override
    public List<GroupSlot> findGroupSlots(SlotQuery slotQuery) {
        List<Jumpday> jumpdayList = jumpdayRepository.findAllAfterIncludingDate(LocalDate.now());
        List<GroupSlot> groupSlots = new ArrayList<>();

        jumpdayList.forEach(jumpday -> {
            int slotCount = jumpday.getSlots().size();
            for (int i = 0; i < slotCount; i++) {
                GroupSlot groupSlot = calculateGroupSlot(jumpday, i, slotQuery.getTandem());
                if (groupSlot != null) {
                    groupSlots.add(groupSlot);
                }
            }
        });

        return groupSlots;
    }

    @Override
    public List<Appointment> findAppointmentsWithinNextWeek() {
        List<Appointment> appointments = new ArrayList<>();
        LocalDate date = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        while (date.isBefore(endDate)) {
            appointments.addAll(findAppointmentsByDay(date));
            date = date.plusDays(1);
        }
        return appointments;
    }

    @Override
    public void reminderSent(Appointment appointment) {
        appointment.setReminderSent(true);
        updateAppointment(appointment, true);
    }

    @Override
    public Appointment confirmAppointment(int appointmentId, String token) {
        Appointment appointment = findAppointment(appointmentId);

        if (appointment == null) {
            log.error("Appointment {} not found", appointmentId);
            throw new NotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND);
        }

        if (!AppointmentState.UNCONFIRMED.equals(appointment.getState())) {
            log.error("Appointment {} already confirmed", appointmentId);
            throw new AlreadyConfirmedException(ErrorMessage.APPOINTMENT_ALREADY_CONFIRMED);
        }

        if (!VerificationTokenUtil.isValid(token, appointment.getVerificationToken())) {
            log.error("Appointment {} token invalid: {}", appointmentId, token);
            throw new InvalidRequestException(ErrorMessage.APPOINTMENT_CONFIRMATION_TOKEN_INVALID);
        }

        updateAppointmentState(appointment, AppointmentState.CONFIRMED);

        return appointment;
    }

    private GroupSlot calculateGroupSlot(Jumpday jumpday, int slotIndex, int minTandemAvailable) {
        GroupSlot groupSlot = new GroupSlot();
        groupSlot.setDate(jumpday.getDate());

        for (int i = slotIndex; i < jumpday.getSlots().size(); i++) {
            Slot slot = jumpday.getSlots().get(i);
            if (slot.getTandemAvailable() == 0) {
                return null;
            }

            SimpleSlot simpleSlot = SimpleSlot.fromSlot(slot);
            groupSlot.getSlots().add(simpleSlot);

            if (groupSlot.getTandemAvailable() >= minTandemAvailable) {
                return groupSlot;
            }
        }

        return null;
    }

    private boolean isInFuture(Jumpday jumpday) {
        return jumpday.getDate().isAfter(LocalDate.now());
    }

    private boolean isTodayButTimeInFuture(Jumpday jumpday, Slot slot) {
        return jumpday.getDate().isEqual(LocalDate.now()) && slot.getTime().isAfter(LocalTime.now(clock));
    }

    private Appointment saveAppointmentInternal(Jumpday jumpday, Appointment appointment, boolean isAdminBooking) {
        if (jumpday == null) {
            log.error("Jumpday not found");
            throw new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }

        if ((appointment.getPicOrVid() + appointment.getPicAndVid() + appointment.getHandcam())
                > appointment.getTandem()) {
            log.error("More video than tandem slots: {}", appointment);
            throw new InvalidRequestException(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS);
        }

        if (!isAdminBooking && appointment.getTandem() != appointment.getCustomer().getJumpers().size()) {
            log.error("Appointment missing jumper info: {}", appointment);
            throw new InvalidRequestException(ErrorMessage.APPOINTMENT_MISSING_JUMPER_INFO);
        }

        if (isAdminBooking) {
            appointment.setState(AppointmentState.CONFIRMED);
        }

        if (!hasSlotsAvailable(jumpday, appointment)) {
            log.error("Jumpday has no free slots for appointment. Jumpday: {}, Appointment: {}", jumpday, appointment);
            throw new NoSlotsLeftException(ErrorMessage.JUMPDAY_NO_FREE_SLOTS);
        }

        if (appointment.getAppointmentId() == 0) {
            appointment.setAppointmentId(sequenceService.getNextSequence(APPOINTMENT_SEQUENCE));
        }
        appointment.setCreatedOn(LocalDateTime.now());
        if (jumpday.addAppointment(appointment)) {
            jumpdayRepository.save(jumpday);
        }

        return appointment;
    }

    private boolean hasSlotsAvailable(Jumpday jumpday, Appointment appointment) {
        Optional<Slot> slotOptional = jumpday.getSlotForAppointment(appointment);
        if (slotOptional.isPresent()) {
            Slot slot = slotOptional.get();
            return slot.getTandemAvailable() >= appointment.getTandem()
                    && slot.getPicOrVidAvailable() >= appointment.getPicOrVid()
                    && slot.getPicAndVidAvailable() >= appointment.getPicAndVid()
                    && slot.getHandcamAvailable() >= appointment.getHandcam()
                    && enoughCombinedSlotsAvailable(slot, appointment);
        }
        return false;
    }

    private boolean enoughCombinedSlotsAvailable(Slot slot, Appointment appointment) {
        return slot.getPicOrVidAvailable() >= appointment.getPicAndVid() + appointment.getPicOrVid();
    }
}
