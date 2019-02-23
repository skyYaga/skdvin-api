package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AppointmentConverterTest {

    private AppointmentConverter converter = new AppointmentConverter();

    @Test
    public void convertToDto() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        AppointmentDTO appointmentDTO = converter.convertToDto(appointment);

        Assert.assertEquals(appointment.getDate(), appointmentDTO.getDate());
        Assert.assertEquals(appointment.getCustomer(), appointmentDTO.getCustomer());
        Assert.assertEquals(appointment.getTandem(), appointmentDTO.getTandem());
        Assert.assertEquals(appointment.getVideo(), appointmentDTO.getVideo());
    }

    @Test
    public void convertToDtoList() {
        Appointment appointment1 = ModelMockHelper.createSingleAppointment();
        Appointment appointment2 = ModelMockHelper.createSecondAppointment();
        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

        List<AppointmentDTO> appointmentDTOList = converter.convertToDto(appointments);

        Assert.assertEquals(appointments.size(), appointmentDTOList.size());
        Assert.assertEquals(appointment1.getCustomer(), appointmentDTOList.get(0).getCustomer());
        Assert.assertEquals(appointment2.getTandem(), appointmentDTOList.get(1).getTandem());
    }

    @Test
    public void convertToEntity() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        AppointmentDTO appointmentDTO = converter.convertToDto(appointment);

        appointment = converter.convertToEntity(appointmentDTO);

        Assert.assertEquals(appointmentDTO.getDate(), appointment.getDate());
        Assert.assertEquals(appointmentDTO.getCustomer(), appointment.getCustomer());
        Assert.assertEquals(appointmentDTO.getTandem(), appointment.getTandem());
        Assert.assertEquals(appointmentDTO.getVideo(), appointment.getVideo());

    }

    @Test
    public void convertToDto_Null() {
        Appointment appointment = null;
        Assert.assertNull(converter.convertToDto(appointment));
    }

    @Test
    public void convertToDtoList_Null() {
        List<Appointment> appointments = null;
        Assert.assertEquals( 0, converter.convertToDto(appointments).size());
    }

    @Test
    public void convertToEntity_Null() {
        Assert.assertNull(converter.convertToEntity(null));
    }
}