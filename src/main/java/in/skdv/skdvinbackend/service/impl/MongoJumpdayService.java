package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidDeletionException;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Slot;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MongoJumpdayService implements IJumpdayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoJumpdayService.class);

    private JumpdayRepository jumpdayRepository;

    @Autowired
    public MongoJumpdayService(JumpdayRepository jumpdayRepository) {
        this.jumpdayRepository = jumpdayRepository;
    }

    @Override
    public GenericResult<List<Jumpday>> findJumpdays() {
        try {
            List<Jumpday> jumpdays = jumpdayRepository.findAll();
            return new GenericResult<>(true, jumpdays);
        } catch (Exception e) {
            LOGGER.error("Error finding jumpdays", e);
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG, e);
        }
    }

    @Override
    public GenericResult<Jumpday> findJumpday(LocalDate date) {
        try {
            Jumpday jumpday = jumpdayRepository.findByDate(date);
            if (jumpday == null) {
                return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
            }
            return new GenericResult<>(true, jumpday);
        } catch (Exception e) {
            LOGGER.error("Error finding jumpday", e);
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG, e);
        }
    }

    @Override
    public GenericResult<Jumpday> saveJumpday(Jumpday jumpday) {
        if (isInvalidJumpday(jumpday)) {
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_INVALID);
        }
        try {
            Jumpday existingJumpday = jumpdayRepository.findByDate(jumpday.getDate());
            if (existingJumpday != null) {
                return new GenericResult<>(false, ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG);
            }
            jumpday = jumpdayRepository.save(jumpday);
            return new GenericResult<>(true, jumpday);
        } catch (Exception e) {
            LOGGER.error("Error saving jumpday", e);
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG, e);
        }
    }

    @Override
    public GenericResult<Jumpday> updateJumpday(LocalDate date, Jumpday changedJumpday) {
        if (isInvalidJumpday(changedJumpday)) {
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_INVALID);
        }
        try {
            Jumpday jumpday = jumpdayRepository.findByDate(date);
            if (jumpday == null) {
                return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
            }
            updateJumpdayInternal(jumpday, changedJumpday);
            Jumpday updatedJumpday = jumpdayRepository.save(jumpday);
            return new GenericResult<>(true, updatedJumpday);
        } catch (InvalidDeletionException e) {
            LOGGER.error("Error updating jumpday", e);
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_SLOT_HAS_APPOINTMENTS);
        }
    }

    private void updateJumpdayInternal(Jumpday existingJumpday, Jumpday changedJumpday) throws InvalidDeletionException {
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
                        throw new InvalidDeletionException("The slot sizes can't be reduced due to existing appointments");
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

    private void removeDeletedSlots(Jumpday existingJumpday, Jumpday changedJumpday) throws InvalidDeletionException {
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
                    throw new InvalidDeletionException("The slot to remove still has appointments");
                }
                iterator.remove();
            }
        }
    }

    private boolean isInvalidJumpday(Jumpday jumpday) {
        for (Slot slot : jumpday.getSlots()) {
            if (slot.getTandemTotal() < slot.getPicOrVidTotal() ||
                    slot.getTandemTotal() < slot.getPicAndVidTotal() ||
                    slot.getTandemTotal() < slot.getHandcamTotal()) {
                return true;
            }
        }
        return false;
    }
}
