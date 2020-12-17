package in.skdv.skdvinbackend.model.common;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupSlotTest {

    @Test
    void testGroupSlot() {
        LocalTime firstTime = LocalTime.of(9, 0);
        LocalTime lastTime = LocalTime.of(11, 30);
        GroupSlot groupSlot = new GroupSlot();
        SimpleSlot simpleSlot1 = new SimpleSlot();
        simpleSlot1.setTandemAvailable(4);
        simpleSlot1.setPicOrVidAvailable(2);
        simpleSlot1.setPicAndVidAvailable(1);
        simpleSlot1.setHandcamAvailable(0);
        simpleSlot1.setTime(firstTime);
        SimpleSlot simpleSlot2 = new SimpleSlot();
        simpleSlot2.setTandemAvailable(3);
        simpleSlot2.setPicOrVidAvailable(1);
        simpleSlot2.setPicAndVidAvailable(1);
        simpleSlot2.setHandcamAvailable(1);
        simpleSlot2.setTime(lastTime);

        groupSlot.setSlots(Arrays.asList(simpleSlot1, simpleSlot2));

        assertEquals(firstTime, groupSlot.getFirstTime());
        assertEquals(lastTime, groupSlot.getLastTime());
        assertEquals(2, groupSlot.getTimeCount());
        assertEquals(7, groupSlot.getTandemAvailable());
        assertEquals(3, groupSlot.getPicOrVidAvailable());
        assertEquals(2, groupSlot.getPicAndVidAvailable());
        assertEquals(1, groupSlot.getHandcamAvailable());
    }
}
