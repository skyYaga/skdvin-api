package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AppointmentMapperTest {

    private final AppointmentMapper mapper = new AppointmentMapperImpl();

    @Test
    void convertToDto() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        AppointmentDTO appointmentDTO = mapper.toDto(appointment);

        assertEquals(appointment.getDate(), appointmentDTO.getDate());
        assertEquals(appointment.getCustomer(), appointmentDTO.getCustomer());
        assertEquals(appointment.getTandem(), appointmentDTO.getTandem());
        assertEquals(appointment.getPicOrVid(), appointmentDTO.getPicOrVid());
    }

    @Test
    void convertToDtoList() {
        Appointment appointment1 = ModelMockHelper.createSingleAppointment();
        Appointment appointment2 = ModelMockHelper.createSecondAppointment();
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

        List<AppointmentDTO> appointmentDTOList = mapper.toDto(appointments);

        assertEquals(appointments.size(), appointmentDTOList.size());
        assertEquals(appointment1.getCustomer(), appointmentDTOList.get(0).getCustomer());
        assertEquals(appointment2.getTandem(), appointmentDTOList.get(1).getTandem());
    }

    @Test
    void convertToEntity() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        AppointmentDTO appointmentDTO = mapper.toDto(appointment);

        appointment = mapper.toEntity(appointmentDTO);

        assertEquals(appointmentDTO.getDate(), appointment.getDate());
        assertEquals(appointmentDTO.getCustomer(), appointment.getCustomer());
        assertEquals(appointmentDTO.getTandem(), appointment.getTandem());
        assertEquals(appointmentDTO.getPicOrVid(), appointment.getPicOrVid());

    }

    @Test
    void convertToDto_Null() {
        Appointment appointment = null;
        assertNull(mapper.toDto(appointment));
    }

    @Test
    void convertToDtoList_Null() {
        List<Appointment> appointments = null;
        assertEquals( 0, mapper.toDto(appointments).size());
    }

    @Test
    void convertToEntity_Null() {
        assertNull(mapper.toEntity(null));
    }
}