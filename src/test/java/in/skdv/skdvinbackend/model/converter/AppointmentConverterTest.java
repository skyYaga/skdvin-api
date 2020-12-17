package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppointmentConverterTest {

    private AppointmentConverter converter = new AppointmentConverter();

    @Test
    public void convertToDto() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        AppointmentDTO appointmentDTO = converter.convertToDto(appointment);

        assertEquals(appointment.getDate(), appointmentDTO.getDate());
        assertEquals(appointment.getCustomer(), appointmentDTO.getCustomer());
        assertEquals(appointment.getTandem(), appointmentDTO.getTandem());
        assertEquals(appointment.getPicOrVid(), appointmentDTO.getPicOrVid());
    }

    @Test
    public void convertToDtoList() {
        Appointment appointment1 = ModelMockHelper.createSingleAppointment();
        Appointment appointment2 = ModelMockHelper.createSecondAppointment();
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

        List<AppointmentDTO> appointmentDTOList = converter.convertToDto(appointments);

        assertEquals(appointments.size(), appointmentDTOList.size());
        assertEquals(appointment1.getCustomer(), appointmentDTOList.get(0).getCustomer());
        assertEquals(appointment2.getTandem(), appointmentDTOList.get(1).getTandem());
    }

    @Test
    public void convertToEntity() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        AppointmentDTO appointmentDTO = converter.convertToDto(appointment);

        appointment = converter.convertToEntity(appointmentDTO);

        assertEquals(appointmentDTO.getDate(), appointment.getDate());
        assertEquals(appointmentDTO.getCustomer(), appointment.getCustomer());
        assertEquals(appointmentDTO.getTandem(), appointment.getTandem());
        assertEquals(appointmentDTO.getPicOrVid(), appointment.getPicOrVid());

    }

    @Test
    public void convertToDto_Null() {
        Appointment appointment = null;
        assertNull(converter.convertToDto(appointment));
    }

    @Test
    public void convertToDtoList_Null() {
        List<Appointment> appointments = null;
        assertEquals( 0, converter.convertToDto(appointments).size());
    }

    @Test
    public void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }
}