package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.converter.AppointmentConverter;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.dto.AppointmentStateOnlyDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.util.GenericResult;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentController.class);

    private IAppointmentService appointmentService;
    private IEmailService emailService;
    private MessageSource messageSource;
    private AppointmentConverter appointmentConverter = new AppointmentConverter();

    @Autowired
    public AppointmentController(IAppointmentService appointmentService, IEmailService emailService, MessageSource messageSource) {
        this.appointmentService = appointmentService;
        this.messageSource = messageSource;
        this.emailService = emailService;
    }

    @GetMapping(value = "/{appointmentId}")
    @PreAuthorize("hasAuthority('SCOPE_read:appointments')")
    public AppointmentDTO readAppointment(@PathVariable int appointmentId) {
        Appointment appointment = appointmentService.findAppointment(appointmentId);
        return appointmentConverter.convertToDto(appointment);
    }

    @PostMapping
    public ResponseEntity<GenericResult> addAppointment(@RequestBody @Valid AppointmentDTO input, HttpServletResponse response) {
        Appointment appointment = appointmentConverter.convertToEntity(input);
        appointment.setVerificationToken(VerificationTokenUtil.generate());

        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);

        if (result.isSuccess()) {
            try {
                emailService.sendAppointmentVerification(result.getPayload());
            } catch (MessagingException e) {
                LOGGER.error("Error sending appointment verification mail", e);
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResult<>(true, appointmentConverter.convertToDto(result.getPayload())));
        }

        return sendSaveOrUpdateErrorResponse(result);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_update:appointments')")
    public ResponseEntity<GenericResult> updateAppointment(@RequestBody @Valid AppointmentDTO input) {
        GenericResult<Appointment> result = appointmentService.updateAppointment(appointmentConverter.convertToEntity(input));

        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true, appointmentConverter.convertToDto(result.getPayload())));
        }

        return sendSaveOrUpdateErrorResponse(result);
    }

    private ResponseEntity<GenericResult> sendSaveOrUpdateErrorResponse(GenericResult<Appointment> result) {
        if (result.getMessage().equals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString()) ||
                result.getMessage().equals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString()) ||
                result.getMessage().equals(ErrorMessage.APPOINTMENT_MISSING_JUMPER_INFO.toString())) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        if (result.getMessage().equals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString())) {
            return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                    .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        LOGGER.warn("Error adding Appointment: {}", result.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }

    @GetMapping(value = "/slots")
    public ResponseEntity<GenericResult> findFreeSlots(SlotQuery input) {
        GenericResult<List<FreeSlot>> result = appointmentService.findFreeSlots(input);

        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true, result.getPayload()));
        }

        if (result.getMessage().equals(ErrorMessage.APPOINTMENT_NO_FREE_SLOTS.toString())) {
            return ResponseEntity.status(HttpServletResponse.SC_OK)
                    .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        if (result.getMessage().equals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString())) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        LOGGER.warn("Error finding free slot: {}", result.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }

    @GetMapping(value = "/{appointmentId}/confirm/{token}")
    public ResponseEntity<GenericResult<Void>> confirmAppointment(@PathVariable int appointmentId, @PathVariable String token) {
        Appointment appointment = appointmentService.findAppointment(appointmentId);
        if (appointment != null) {
            if (AppointmentState.UNCONFIRMED.equals(appointment.getState())) {
                if (VerificationTokenUtil.isValid(token, appointment.getVerificationToken())) {
                    GenericResult<Void> result = appointmentService.updateAppointmentState(appointment, AppointmentState.CONFIRMED);
                    if (result.isSuccess()) {
                        try {
                            emailService.sendAppointmentConfirmation(appointment);
                        } catch (MessagingException e) {
                            LOGGER.error("Error sending appointment verification mail", e);
                        }
                        return ResponseEntity.ok(new GenericResult<>(true));
                    }
                }
                return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                        .body(new GenericResult<>(false, messageSource.getMessage(
                                ErrorMessage.APPOINTMENT_CONFIRMATION_TOKEN_INVALID.toString(), null, LocaleContextHolder.getLocale())));
            }
            return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                    .body(new GenericResult<>(false, messageSource.getMessage(
                            ErrorMessage.APPOINTMENT_ALREADY_CONFIRMED.toString(), null, LocaleContextHolder.getLocale())));
        }
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(
                        ErrorMessage.APPOINTMENT_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
    }

    @GetMapping(value = "/date/{date}")
    @PreAuthorize("hasAuthority('SCOPE_read:appointments')")
    public ResponseEntity<GenericResult> getAppointmentsByDate(@PathVariable String date) {
        List<Appointment> appointments = appointmentService.findAppointmentsByDay(LocalDate.parse(date));
        if (appointments != null) {
            return ResponseEntity.ok(new GenericResult<>(true, appointmentConverter.convertToDto(appointments)));
        }

        LOGGER.warn("Error reading Appointments");
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(ErrorMessage.APPOINTMENT_SERVICE_ERROR_MSG.toString(), null, LocaleContextHolder.getLocale())));
    }


    @PatchMapping(value = "/{appointmentId}")
    @PreAuthorize("hasAuthority('SCOPE_update:appointments')")
    public ResponseEntity<GenericResult<Void>> updateAppointmentState(@RequestBody @Valid AppointmentStateOnlyDTO appointmentStateOnly, @PathVariable int appointmentId) {
        Appointment appointment = appointmentService.findAppointment(appointmentId);
        if (appointment != null) {
            GenericResult<Void> result = appointmentService.updateAppointmentState(appointment, appointmentStateOnly.getState());
            if (result.isSuccess()) {
                return ResponseEntity.ok(new GenericResult<>(true));
            }
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new GenericResult<>(false, messageSource.getMessage(ErrorMessage.APPOINTMENT_SERVICE_ERROR_MSG.toString(), null, LocaleContextHolder.getLocale())));
        }
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(
                        ErrorMessage.APPOINTMENT_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
    }
}
