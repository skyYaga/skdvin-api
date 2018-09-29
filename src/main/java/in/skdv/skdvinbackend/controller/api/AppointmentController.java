package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.service.IAppointmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private IAppointmentService appointmentService;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AppointmentController(IAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    List<AppointmentDTO> readAppointments() {
        return convertToDto(appointmentService.findAppointments());
    }

    @GetMapping(value = "/{appointmentId}")
    AppointmentDTO readAppointment(@PathVariable int appointmentId) {
        Appointment appointment = appointmentService.findAppointment(appointmentId);
        return convertToDto(appointment);
    }

    @PostMapping
    AppointmentDTO addAppointment(@RequestBody AppointmentDTO input, HttpServletResponse response) {
        Appointment appointment = appointmentService.saveAppointment(convertToEntity(input));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return convertToDto(appointment);
    }

    @PutMapping
    AppointmentDTO updateAppointment(@RequestBody AppointmentDTO input) {
        Appointment appointment = appointmentService.updateAppointment(convertToEntity(input));
        return convertToDto(appointment);
    }

    private AppointmentDTO convertToDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        return modelMapper.map(appointment, AppointmentDTO.class);
    }


    private List<AppointmentDTO> convertToDto(List<Appointment> appointments) {
        if (appointments == null) {
            return null;
        }
        List<AppointmentDTO> appointmentDTOList = new ArrayList<>();
        appointments.forEach(a -> appointmentDTOList.add(this.convertToDto(a)));
        return appointmentDTOList;
    }

    private Appointment convertToEntity(AppointmentDTO appointmentDto) {
        if (appointmentDto == null) {
            return null;
        }
        return modelMapper.map(appointmentDto, Appointment.class);
    }
}
