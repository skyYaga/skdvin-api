package in.skdv.skdvinbackend.model.common;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlotQueryTest {

    @Test
    public void testSlotQuery_Valid() {
        assertTrue(new SlotQuery(3, 1, 1, 0).isValid());
    }

    @Test
    public void testSlotQuery_Invalid() {
        assertFalse(new SlotQuery(2, 1, 1, 1).isValid());
        assertFalse(new SlotQuery(2, 3, 0, 0).isValid());
    }
}
