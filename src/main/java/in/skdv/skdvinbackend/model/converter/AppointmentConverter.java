package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppointmentConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public AppointmentDTO convertToDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        return modelMapper.map(appointment, AppointmentDTO.class);
    }


    public List<AppointmentDTO> convertToDto(List<Appointment> appointments) {
        if (appointments == null) {
            return Collections.emptyList();
        }
        List<AppointmentDTO> appointmentDTOList = new ArrayList<>();
        appointments.forEach(a -> appointmentDTOList.add(this.convertToDto(a)));
        return appointmentDTOList;
    }

    public Appointment convertToEntity(AppointmentDTO appointmentDto) {
        if (appointmentDto == null) {
            return null;
        }
        return modelMapper.map(appointmentDto, Appointment.class);
    }
}
