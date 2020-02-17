package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Slot;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.ISequenceService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MongoAppointmentService implements IAppointmentService {

    private static final String APPOINTMENT_SEQUENCE = "appointment";

    private JumpdayRepository jumpdayRepository;
    private ISequenceService sequenceService;
    private Clock clock = Clock.systemDefaultZone();

    @Autowired
    public MongoAppointmentService(JumpdayRepository jumpdayRepository, ISequenceService sequenceService) {
        this.jumpdayRepository = jumpdayRepository;
        this.sequenceService = sequenceService;
    }

    @Override
    public GenericResult<Appointment> saveAppointment(Appointment appointment) {
        Jumpday jumpday = jumpdayRepository.findByDate(appointment.getDate().toLocalDate());
        return saveAppointmentInternal(jumpday, appointment);
    }


    @Override
    public GenericResult<Appointment> updateAppointment(Appointment newAppointment) {
        Appointment oldAppointment = findAppointment(newAppointment.getAppointmentId());

        if (oldAppointment == null) {
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }

        if (oldAppointment.getDate().toLocalDate().equals(newAppointment.getDate().toLocalDate())) {
            // Delete Appointment in this jumpday and create a new one, then save
            Jumpday jumpday = jumpdayRepository.findByDate(newAppointment.getDate().toLocalDate());
            for (Slot slot : jumpday.getSlots()) {

                slot.getAppointments().removeIf(appointment -> appointment.getAppointmentId() == newAppointment.getAppointmentId());
            }

            return saveAppointmentInternal(jumpday, newAppointment);
        }

        // Create new Appointment and save it and then delete old one and save
        GenericResult<Appointment> appointmentGenericResult = saveAppointment(newAppointment);

        if (appointmentGenericResult.isSuccess()) {

            Jumpday jumpday = jumpdayRepository.findByDate(oldAppointment.getDate().toLocalDate());
            for (Slot slot : jumpday.getSlots()) {

                for (Iterator<Appointment> iterator = slot.getAppointments().iterator(); iterator.hasNext();) {
                    Appointment appointment = iterator.next();

                    if (appointment.getAppointmentId() == newAppointment.getAppointmentId()) {
                        iterator.remove();
                        jumpdayRepository.save(jumpday);
                    }
                }
            }
        }
        return appointmentGenericResult;
    }

    @Override
    public Appointment findAppointment(int id) {
        List<Jumpday> jumpdayList = jumpdayRepository.findAll();

        Optional<Appointment> appointment = jumpdayList.stream()
                .flatMap(day -> day.getSlots().stream())
                .flatMap(s -> s.getAppointments().stream())
                .filter(a -> a.getAppointmentId() == id).findFirst();

        return appointment.orElse(null);
    }

    @Override
    public List<Appointment> findAppointmentsByDay(LocalDate date) {
        return jumpdayRepository.findByDate(date).getSlots().stream()
                .flatMap(s -> s.getAppointments().stream()).collect(Collectors.toList());
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
                        .filter(slot -> slot.getTime().isAfter(LocalTime.now(clock)) &&
                                slot.isValidForQuery(slotQuery))
                        .map(Slot::getTime)
                        .collect(Collectors.toList());

                if (slotTimes.size() > 0) {
                    resultList.add(new FreeSlot(jumpday.getDate(), slotTimes));
                }
            }
        });

        if (resultList.size() > 0) {
            return new GenericResult<>(true, resultList);
        }
        return new GenericResult<>(false, ErrorMessage.APPOINTMENT_NO_FREE_SLOTS);
    }

    private GenericResult<Appointment> saveAppointmentInternal(Jumpday jumpday, Appointment appointment) {
        if (jumpday == null) {
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }

        if ((appointment.getPicOrVid() + appointment.getPicAndVid() + appointment.getHandcam())
                > appointment.getTandem()) {
            return new GenericResult<>(false, ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS);
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
