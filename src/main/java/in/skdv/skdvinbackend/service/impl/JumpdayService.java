package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidDeletionException;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Slot;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class JumpdayService implements IJumpdayService {

    private final JumpdayRepository jumpdayRepository;

    @Override
    public List<Jumpday> findJumpdays() {
        return jumpdayRepository.findAll();
    }

    @Override
    public List<Jumpday> findJumpdaysByMonth(YearMonth yearMonth) {
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        return jumpdayRepository.findInRange(firstDayOfMonth, lastDayOfMonth);
    }

    @Override
    public Jumpday findJumpday(LocalDate date) {
        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday == null) {
            log.error("Jumpday {} does not exist", date);
            throw new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        return jumpday;
    }

    @Override
    @Transactional
    public Jumpday saveJumpday(Jumpday jumpday) {
        checkValidJumpday(jumpday);

        Jumpday existingJumpday = jumpdayRepository.findByDate(jumpday.getDate());
        if (existingJumpday != null) {
            log.error("Jumpday {} does not exist", jumpday.getDate());
            throw new InvalidRequestException(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG);
        }

        return jumpdayRepository.save(jumpday);
    }

    @Override
    @Transactional
    public Jumpday updateJumpday(LocalDate date, Jumpday changedJumpday) {
        checkValidJumpday(changedJumpday);

        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday == null) {
            log.error("Jumpday {} does not exist", date);
            throw new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }

        updateJumpdayInternal(jumpday, changedJumpday);
        return jumpdayRepository.save(jumpday);
    }

    @Override
    @Transactional
    public void deleteJumpday(LocalDate date) {
        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday == null) {
            log.error("Jumpday {} does not exist", date);
            throw new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        checkJumpdayHasNoAppointments(jumpday);

        jumpdayRepository.deleteByDate(date);
    }

    private void checkJumpdayHasNoAppointments(Jumpday jumpday) {
        for (Slot slot : jumpday.getSlots()) {
            if (!slot.getAppointments().isEmpty()) {
                log.error("Jumpday {} still has appointments", jumpday);
                throw new InvalidDeletionException(ErrorMessage.JUMPDAY_HAS_APPOINTMENTS);
            }
        }
    }

    private void updateJumpdayInternal(Jumpday existingJumpday, Jumpday changedJumpday) {
        removeDeletedSlots(existingJumpday, changedJumpday);
        updateExistingSlots(existingJumpday, changedJumpday);
        addNewSlots(existingJumpday, changedJumpday);
    }

    private void updateExistingSlots(Jumpday existingJumpday, Jumpday changedJumpday) throws InvalidDeletionException {
        for (Slot slot : changedJumpday.getSlots()) {
            for (Slot existingSlot : existingJumpday.getSlots()) {
                if (existingSlot.getTime().equals(slot.getTime())) {
                    if (containsNotMoreBookingsThanNewSlots(existingSlot, slot)) {
                        existingSlot.setTandemTotal(slot.getTandemTotal());
                        existingSlot.setPicOrVidTotal(slot.getPicOrVidTotal());
                        existingSlot.setPicAndVidTotal(slot.getPicAndVidTotal());
                        existingSlot.setHandcamTotal(slot.getHandcamTotal());
                    } else {
                        log.error("The slot sizes can't be reduced due to existing appointments");
                        throw new InvalidDeletionException(ErrorMessage.JUMPDAY_SLOT_HAS_APPOINTMENTS);
                    }
                }
            }
        }
    }

    private boolean containsNotMoreBookingsThanNewSlots(Slot existingSlot, Slot slot) {
        return existingSlot.getTandemBooked() <= slot.getTandemTotal()
                && existingSlot.getPicOrVidBooked() <= slot.getPicOrVidTotal()
                && existingSlot.getPicAndVidBooked() <= slot.getPicAndVidTotal()
                && existingSlot.getHandcamBooked() <= slot.getHandcamTotal();
    }

    private void addNewSlots(Jumpday existingJumpday, Jumpday changedJumpday) {
        changedJumpday.getSlots().forEach(slot -> {
            AtomicBoolean foundSlot = new AtomicBoolean(false);
            existingJumpday.getSlots().forEach(existingSlot -> {
                if (existingSlot.getTime().equals(slot.getTime())) {
                    foundSlot.set(true);
                }
            });

            if (!foundSlot.get()) {
                existingJumpday.getSlots().add(slot);
                existingJumpday.getSlots().sort(Comparator.comparing(Slot::getTime));
            }
        });
    }

    private void removeDeletedSlots(Jumpday existingJumpday, Jumpday changedJumpday) {
        Iterator<Slot> iterator = existingJumpday.getSlots().iterator();
        while (iterator.hasNext()) {
            Slot slot = iterator.next();
            AtomicBoolean foundSlot = new AtomicBoolean(false);
            changedJumpday.getSlots().forEach(changedSlot -> {
                if (changedSlot.getTime().equals(slot.getTime())) {
                    foundSlot.set(true);
                }
            });

            if (!foundSlot.get()) {
                if (!slot.getAppointments().isEmpty()) {
                    log.error("The slot to remove still has appointments");
                    throw new InvalidDeletionException(ErrorMessage.JUMPDAY_SLOT_HAS_APPOINTMENTS);
                }
                iterator.remove();
            }
        }
    }

    private void checkValidJumpday(Jumpday jumpday) {
        for (Slot slot : jumpday.getSlots()) {
            if (slot.getTandemTotal() < slot.getPicOrVidTotal() ||
                    slot.getTandemTotal() < slot.getPicAndVidTotal() ||
                    slot.getPicOrVidTotal() < slot.getPicAndVidTotal() ||
                    slot.getTandemTotal() < slot.getHandcamTotal()) {
                log.error("Jumpday is invalid: {}", jumpday);
                throw new InvalidRequestException(ErrorMessage.JUMPDAY_INVALID);
            }
        }
    }
}
