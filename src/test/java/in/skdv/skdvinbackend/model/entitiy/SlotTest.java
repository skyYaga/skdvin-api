package in.skdv.skdvinbackend.model.entitiy;

import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Slot;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlotTest {

    private Slot slot;

    @Before
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
}
