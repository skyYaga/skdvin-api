package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.Appointment;
import in.skdv.skdvinbackend.service.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private IAppointmentService appointmentService;

    @Autowired
    public AppointmentController(IAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    List<Appointment> readAppointments() {
        return appointmentService.findAppointments();
    }

    @GetMapping(value = "/{appointmentId}")
    Appointment readAppointment(@PathVariable int appointmentId) {
        return appointmentService.findAppointment(appointmentId);
    }

    @PostMapping
    Appointment addAppointment(@RequestBody Appointment input, HttpServletResponse response) {
        Appointment appointment = appointmentService.saveAppointment(input);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return appointment;
    }

    @PutMapping
    Appointment updateAppointment(@RequestBody Appointment input) {
        return appointmentService.updateAppointment(input);
    }

}
