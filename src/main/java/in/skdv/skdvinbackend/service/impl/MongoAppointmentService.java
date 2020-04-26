package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.GroupSlot;
import in.skdv.skdvinbackend.model.common.SimpleSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Slot;
import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.ISequenceService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class MongoAppointmentService implements IAppointmentService {

    private static final String APPOINTMENT_SEQUENCE = "appointment";

    private final MongoTemplate mongoTemplate;
    private final AppointmentRepository appointmentRepository;
    private JumpdayRepository jumpdayRepository;
    private ISequenceService sequenceService;
    private Clock clock = Clock.systemDefaultZone();

    @Autowired
    public MongoAppointmentService(JumpdayRepository jumpdayRepository, AppointmentRepository appointmentRepository,
                                   ISequenceService sequenceService, MongoTemplate mongoTemplate) {
        this.jumpdayRepository = jumpdayRepository;
        this.appointmentRepository = appointmentRepository;
        this.sequenceService = sequenceService;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public GenericResult<Appointment> saveAppointment(Appointment appointment) {
        Jumpday jumpday = jumpdayRepository.findByDate(appointment.getDate().toLocalDate());
        return saveAppointmentInternal(jumpday, appointment, false);
    }

    @Override
    public GenericResult<Appointment> saveAdminAppointment(Appointment appointment) {
        Jumpday jumpday = jumpdayRepository.findByDate(appointment.getDate().toLocalDate());
        return saveAppointmentInternal(jumpday, appointment, true);
    }

    @Override
    public GenericResult<Appointment> updateAppointment(Appointment newAppointment) {
        return updateAppointment(newAppointment, false);
    }

    @Override
    public GenericResult<Appointment> updateAdminAppointment(Appointment appointment) {
        return updateAppointment(appointment, true);
    }


    private GenericResult<Appointment> updateAppointment(Appointment newAppointment, boolean isAdminBooking) {
        Appointment oldAppointment = findAppointment(newAppointment.getAppointmentId());

        if (oldAppointment == null) {
            return new GenericResult<>(false, ErrorMessage.APPOINTMENT_NOT_FOUND);
        }

        if (isAtSameDateAndTime(newAppointment, oldAppointment)) {
            // Delete Appointment in this jumpday and create a new one, then save
            return replaceAppointment(newAppointment, isAdminBooking);
        }

        // Create new Appointment and save it and then delete old one and save
        GenericResult<Appointment> appointmentGenericResult;
        if (isAdminBooking) {
            appointmentGenericResult = saveAdminAppointment(newAppointment);
        } else {
            appointmentGenericResult = saveAppointment(newAppointment);
        }
        if (appointmentGenericResult.isSuccess()) {
            deleteOldAppointment(newAppointment, oldAppointment);
        }
        return appointmentGenericResult;
    }

    private void deleteOldAppointment(Appointment newAppointment, Appointment oldAppointment) {
        Jumpday jumpday = jumpdayRepository.findByDate(oldAppointment.getDate().toLocalDate());
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

    private GenericResult<Appointment> replaceAppointment(Appointment newAppointment, boolean isAdminBooking) {
        Jumpday jumpday = jumpdayRepository.findByDate(newAppointment.getDate().toLocalDate());
        for (Slot slot : jumpday.getSlots()) {
            slot.getAppointments().removeIf(appointment -> appointment != null
                    && appointment.getAppointmentId() == newAppointment.getAppointmentId());
        }

        return saveAppointmentInternal(jumpday, newAppointment, isAdminBooking);
    }

    private boolean isAtSameDateAndTime(Appointment newAppointment, Appointment oldAppointment) {
        return oldAppointment.getDate().toLocalDate().equals(newAppointment.getDate().toLocalDate());
    }

    @Override
    public Appointment findAppointment(int id) {
        List<Jumpday> jumpdayList = jumpdayRepository.findAll();

        Optional<Appointment> appointment = jumpdayList.stream()
                .flatMap(day -> day.getSlots().stream())
                .flatMap(s -> s.getAppointments().stream())
                .filter(a -> a != null && a.getAppointmentId() == id).findFirst();

        return appointment.orElse(null);
    }

    @Override
    public List<Appointment> findAppointmentsByDay(LocalDate date) {
        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday != null && jumpday.getSlots() != null) {
            return jumpday.getSlots().stream()
                    .flatMap(s -> s.getAppointments().stream().filter(Objects::nonNull)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public GenericResult<List<FreeSlot>> findFreeSlots(SlotQuery slotQuery) {
        if (!slotQuery.isValid()) {
            return new GenericResult<>(false, ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS);
        }

        List<Jumpday> jumpdayList = jumpdayRepository.findAll();
        List<FreeSlot> resultList = new ArrayList<>();

        jumpdayList.forEach(jumpday -> {
            if (!jumpday.getDate().isBefore(LocalDate.now())) {
                List<LocalTime> slotTimes = jumpday.getSlots().stream()
                        .filter(slot ->
                                (isTodayButTimeInFuture(jumpday, slot) || isInFuture(jumpday))
                                        && slot.isValidForQuery(slotQuery)
                        )
                        .map(Slot::getTime)
                        .collect(Collectors.toList());

                if (!slotTimes.isEmpty()) {
                    resultList.add(new FreeSlot(jumpday.getDate(), slotTimes));
                }
            }
        });

        if (!resultList.isEmpty()) {
            return new GenericResult<>(true, resultList);
        }
        return new GenericResult<>(false, ErrorMessage.APPOINTMENT_NO_FREE_SLOTS);
    }

    @Override
    public GenericResult<Void> updateAppointmentState(Appointment appointment, AppointmentState appointmentState) {
        appointment.setState(appointmentState);
        GenericResult<Appointment> result = updateAppointment(appointment);
        if (result.isSuccess()) {
            return new GenericResult<>(true);
        }
        return new GenericResult<>(false, result.getMessage());
    }

    @Override
    public List<Appointment> findUnconfirmedAppointments() {
        List<Jumpday> jumpdayList = jumpdayRepository.findAll();

        return jumpdayList.stream()
                .flatMap(day -> day.getSlots().stream())
                .flatMap(s -> s.getAppointments().stream())
                .filter(a -> a != null && a.getVerificationToken() != null
                        && a.getVerificationToken().getExpiryDate().isBefore(LocalDateTime.now())
                        && a.getState().equals(AppointmentState.UNCONFIRMED))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAppointment(int appointmentId) {
        List<Jumpday> jumpdays = jumpdayRepository.findBySlotsAppointmentsAppointmentId(appointmentId);

        jumpdays.forEach(jumpday -> jumpday.getSlots().forEach(slot -> {
            slot.getAppointments().removeIf(appointment -> appointment.getAppointmentId() == appointmentId);
        }));

        jumpdayRepository.saveAll(jumpdays);
    }

    @Override
    public List<GroupSlot> findGroupSlots(SlotQuery slotQuery) {
        List<Jumpday> jumpdayList = jumpdayRepository.findAll();
        List<GroupSlot> groupSlots = new ArrayList<>();

        jumpdayList.stream().filter(j -> !isInPast(j)).forEach(jumpday -> {
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

    private boolean isInPast(Jumpday jumpday) {
        return jumpday.getDate().isBefore(LocalDate.now());
    }

    private boolean isTodayButTimeInFuture(Jumpday jumpday, Slot slot) {
        return jumpday.getDate().isEqual(LocalDate.now()) && slot.getTime().isAfter(LocalTime.now(clock));
    }

    private GenericResult<Appointment> saveAppointmentInternal(Jumpday jumpday, Appointment appointment, boolean isAdminBooking) {
        if (jumpday == null) {
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }

        if ((appointment.getPicOrVid() + appointment.getPicAndVid() + appointment.getHandcam())
                > appointment.getTandem()) {
            return new GenericResult<>(false, ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS);
        }

        if (!isAdminBooking && appointment.getTandem() != appointment.getCustomer().getJumpers().size()) {
            return new GenericResult<>(false, ErrorMessage.APPOINTMENT_MISSING_JUMPER_INFO);
        }

        if (isAdminBooking) {
            appointment.setState(AppointmentState.CONFIRMED);
        }

        if (hasSlotsAvailable(jumpday, appointment)) {
            if (appointment.getAppointmentId() == 0) {
                appointment.setAppointmentId(sequenceService.getNextSequence(APPOINTMENT_SEQUENCE));
            }
            appointment.setCreatedOn(LocalDateTime.now());
            if (jumpday.addAppointment(appointment)) {
                jumpdayRepository.save(jumpday);
                return new GenericResult<>(true, appointment);
            }
        }

        return new GenericResult<>(false, ErrorMessage.JUMPDAY_NO_FREE_SLOTS);
    }

    private boolean hasSlotsAvailable(Jumpday jumpday, Appointment appointment) {
        Optional<Slot> slotOptional = jumpday.getSlotForAppointment(appointment);
        if (slotOptional.isPresent()) {
            Slot slot = slotOptional.get();
            return slot.getTandemAvailable() >= appointment.getTandem()
                    && slot.getPicOrVidAvailable() >= appointment.getPicOrVid()
                    && slot.getPicAndVidAvailable() >= appointment.getPicAndVid()
                    && slot.getHandcamAvailable() >= appointment.getHandcam();
        }
        return false;
    }
}
