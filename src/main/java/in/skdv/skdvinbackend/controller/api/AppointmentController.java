package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.GroupSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.converter.AppointmentConverter;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.dto.AppointmentStateOnlyDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.util.GenericResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final IEmailService emailService;
    private final MessageSource messageSource;
    private final AppointmentConverter appointmentConverter;

    @GetMapping(value = "/{appointmentId}")
    @PreAuthorize("hasAuthority('SCOPE_read:appointments')")
    public GenericResult<AppointmentDTO> readAppointment(@PathVariable int appointmentId) {
        log.info("Reading appointment {}", appointmentId);
        Appointment appointment = appointmentService.findAppointment(appointmentId);
        return new GenericResult<>(true, appointmentConverter.convertToDto(appointment));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll")
    public GenericResult<AppointmentDTO> addAppointment(@RequestBody @Valid AppointmentDTO input) {
        log.info("Adding appointment {}", input);
        Appointment appointment = appointmentConverter.convertToEntity(input);

        Appointment result = appointmentService.saveAppointment(appointment);

        return new GenericResult<>(true, appointmentConverter.convertToDto(result));
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_create:appointments')")
    public GenericResult<AppointmentDTO> addAdminAppointment(@RequestBody @Valid AppointmentDTO input) {
        log.info("Adding admin appointment {}", input);

        Appointment appointment = appointmentConverter.convertToEntity(input);
        Appointment result = appointmentService.saveAdminAppointment(appointment);

        return new GenericResult<>(true, appointmentConverter.convertToDto(result));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_update:appointments')")
    public GenericResult<AppointmentDTO> updateAppointment(@RequestBody @Valid AppointmentDTO input) {
        log.info("Updating appointment {}", input);
        Appointment result = appointmentService.updateAppointment(appointmentConverter.convertToEntity(input));
        return new GenericResult<>(true, appointmentConverter.convertToDto(result));
    }

    @PutMapping("/admin")
    @PreAuthorize("hasAuthority('SCOPE_update:appointments')")
    public GenericResult<AppointmentDTO> updateAdminAppointment(@RequestBody @Valid AppointmentDTO input) {
        log.info("Updating admin appointment {}", input);
        Appointment result = appointmentService.updateAdminAppointment(appointmentConverter.convertToEntity(input));
        return new GenericResult<>(true, appointmentConverter.convertToDto(result));
    }

    @DeleteMapping(value = "/{appointmentId}")
    @PreAuthorize("hasAuthority('SCOPE_update:appointments')")
    public GenericResult<Void> deleteAppointment(@PathVariable int appointmentId) {
        log.info("Deleting appointment {}", appointmentId);
        Appointment appointment = appointmentService.findAppointment(appointmentId);

        if (appointment == null) {
            log.error("Appointment {} not found", appointmentId);
            throw new NotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND);
        }

        appointmentService.deleteAppointment(appointment);
        return new GenericResult<>(true);
    }

    @GetMapping(value = "/slots")
    @PreAuthorize("permitAll")
    public GenericResult<List<FreeSlot>> findFreeSlots(SlotQuery input) {
        log.info("Finding free slots with query {}", input);
        List<FreeSlot> result = appointmentService.findFreeSlots(input);

        if (result.isEmpty()) {
            return new GenericResult<>(false, messageSource.getMessage(ErrorMessage.APPOINTMENT_NO_FREE_SLOTS.toString(), null, LocaleContextHolder.getLocale()));
        }
        return new GenericResult<>(true, result);
    }

    @GetMapping(value = "/{appointmentId}/confirm/{token}")
    @PreAuthorize("permitAll")
    public GenericResult<Void> confirmAppointment(@PathVariable int appointmentId, @PathVariable String token) {
        log.info("Confirm appointment {} with token {}", appointmentId, token);
        appointmentService.confirmAppointment(appointmentId, token);
        return new GenericResult<>(true);
    }

    @GetMapping(value = "/date/{date}")
    @PreAuthorize("hasAuthority('SCOPE_read:appointments')")
    public GenericResult<List<AppointmentDTO>> getAppointmentsByDate(@PathVariable String date) {
        log.info("Getting appointments by date {}", date);
        List<Appointment> appointments = appointmentService.findAppointmentsByDay(LocalDate.parse(date));
        return new GenericResult<>(true, appointmentConverter.convertToDto(appointments));
    }


    @PatchMapping(value = "/{appointmentId}")
    @PreAuthorize("hasAuthority('SCOPE_update:appointments')")
    public GenericResult<Void> updateAppointmentState(@PathVariable int appointmentId, @RequestBody @Valid AppointmentStateOnlyDTO appointmentStateOnly) {
        log.info("Updating appointment {} state {}", appointmentId, appointmentStateOnly);
        Appointment appointment = appointmentService.findAppointment(appointmentId);
        appointmentService.updateAppointmentState(appointment, appointmentStateOnly.getState());
        return new GenericResult<>(true);
    }


    @GetMapping(value = "/groupslots")
    @PreAuthorize("hasAuthority('SCOPE_read:appointments')")
    public GenericResult<List<GroupSlot>> findGroupSlots(@RequestParam("tandem") Integer tandemCount) {
        log.info("Finding group slot with tandem count {}", tandemCount);
        SlotQuery slotQuery = new SlotQuery(tandemCount, 0, 0, 0);
        List<GroupSlot> result = appointmentService.findGroupSlots(slotQuery);
        return new GenericResult<>(true, result);
    }
}
