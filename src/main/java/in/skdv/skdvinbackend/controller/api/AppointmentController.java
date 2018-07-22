package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.Appointment;
import in.skdv.skdvinbackend.service.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
