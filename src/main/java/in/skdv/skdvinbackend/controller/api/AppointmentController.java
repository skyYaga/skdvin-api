package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.AppointmentConverter;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentController.class);

    private IAppointmentService appointmentService;
    private MessageSource messageSource;
    private AppointmentConverter appointmentConverter = new AppointmentConverter();

    @Autowired
    public AppointmentController(IAppointmentService appointmentService, MessageSource messageSource) {
        this.appointmentService = appointmentService;
        this.messageSource = messageSource;
    }

    @GetMapping(value = "/{appointmentId}")
    @PreAuthorize("hasAuthority('SCOPE_read:appointments')")
    public AppointmentDTO readAppointment(@PathVariable int appointmentId) {
        Appointment appointment = appointmentService.findAppointment(appointmentId);
        return appointmentConverter.convertToDto(appointment);
    }

    @PostMapping
    public ResponseEntity<GenericResult> addAppointment(@RequestBody AppointmentDTO input, HttpServletResponse response) {

        GenericResult<Appointment> result = appointmentService.saveAppointment(appointmentConverter.convertToEntity(input));

        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResult<>(true, appointmentConverter.convertToDto(result.getPayload())));
        }

        if (result.getMessage().equals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString()) ||
                result.getMessage().equals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString())) {
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

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_update:appointments')")
    public ResponseEntity<GenericResult> updateAppointment(@RequestBody AppointmentDTO input) {
        GenericResult<Appointment> result = appointmentService.updateAppointment(appointmentConverter.convertToEntity(input));

        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true, appointmentConverter.convertToDto(result.getPayload())));
        }

        if (result.getMessage().equals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString()) ||
                result.getMessage().equals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString())) {
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
}
