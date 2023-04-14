package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface AppointmentMapper {

    AppointmentDTO toDto(Appointment appointment);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<AppointmentDTO> toDto(List<Appointment> appointments);

    Appointment toEntity(AppointmentDTO appointmentDto);
}
