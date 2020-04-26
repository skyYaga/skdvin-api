package in.skdv.skdvinbackend.model.common;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;
import java.util.Arrays;

public class GroupSlotTest {

    @Test
    public void testGroupSlot() {
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

        Assert.assertEquals(firstTime, groupSlot.getFirstTime());
        Assert.assertEquals(lastTime, groupSlot.getLastTime());
        Assert.assertEquals(2, groupSlot.getTimeCount());
        Assert.assertEquals(7, groupSlot.getTandemAvailable());
        Assert.assertEquals(3, groupSlot.getPicOrVidAvailable());
        Assert.assertEquals(2, groupSlot.getPicAndVidAvailable());
        Assert.assertEquals(1, groupSlot.getHandcamAvailable());
    }
}
