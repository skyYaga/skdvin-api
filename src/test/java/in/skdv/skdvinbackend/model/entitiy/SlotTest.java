package in.skdv.skdvinbackend.model.entitiy;

import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Slot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SlotTest {

    private Slot slot;

    @BeforeEach
    public void setup() {
        slot = new Slot();
        slot.setTandemTotal(4);
        slot.setPicOrVidTotal(2);
        slot.setPicAndVidTotal(1);
        slot.setHandcamTotal(1);
    }

    @Test
    public void testIsValidForQuery_Valid() {
        SlotQuery query = new SlotQuery(2, 1, 1, 1);
        assertTrue(slot.isValidForQuery(query));
    }

    @Test
    public void testIsValidForQuery_InvalidTandem() {
        SlotQuery query = new SlotQuery(5, 1, 1, 1);
        assertFalse(slot.isValidForQuery(query));
    }

    @Test
    public void testIsValidForQuery_InvalidPaV() {
        SlotQuery query = new SlotQuery(2, 1, 2, 1);
        assertFalse(slot.isValidForQuery(query));
    }

    @Test
    public void testIsValidForQuery_InvalidPoV() {
        SlotQuery query = new SlotQuery(2, 3, 1, 1);
        assertFalse(slot.isValidForQuery(query));
    }

    @Test
    public void testIsValidForQuery_InvalidHandcam() {
        SlotQuery query = new SlotQuery(2, 1, 1, 2);
        assertFalse(slot.isValidForQuery(query));
    }

    @Test
    public void testPicOrVid_IsReducedWhenPicAndVidBooked() {
        Appointment appointment = new Appointment();
        appointment.setPicAndVid(1);
        appointment.setPicOrVid(0);

        slot.getAppointments().add(appointment);

        assertEquals(1, slot.getPicAndVidBooked());
        assertEquals(0, slot.getPicAndVidAvailable());
        assertEquals(0, slot.getPicOrVidBooked());
        assertEquals(1, slot.getPicOrVidAvailable());
    }

    @Test
    public void testPicAndVid_IsLessOrEqualsPicOrVidAvailable() {
        Appointment appointment = new Appointment();
        appointment.setPicAndVid(0);
        appointment.setPicOrVid(2);

        slot.getAppointments().add(appointment);

        assertEquals(2, slot.getPicOrVidBooked());
        assertEquals(0, slot.getPicOrVidAvailable());
        assertEquals(0, slot.getPicAndVidBooked());
        assertEquals(0, slot.getPicAndVidAvailable());
    }
}
