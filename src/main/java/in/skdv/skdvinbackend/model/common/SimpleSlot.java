package in.skdv.skdvinbackend.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.skdv.skdvinbackend.model.entity.Slot;
import lombok.Data;

import java.time.LocalTime;

@Data
public class SimpleSlot {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private int tandemAvailable;
    private int picOrVidAvailable;
    private int picAndVidAvailable;
    private int handcamAvailable;

    public static SimpleSlot fromSlot(Slot slot) {
        SimpleSlot simpleSlot = new SimpleSlot();
        simpleSlot.setTime(slot.getTime());
        simpleSlot.setTandemAvailable(slot.getTandemAvailable());
        simpleSlot.setPicOrVidAvailable(slot.getPicOrVidAvailable());
        simpleSlot.setPicAndVidAvailable(slot.getPicAndVidAvailable());
        simpleSlot.setHandcamAvailable(slot.getHandcamAvailable());
        return simpleSlot;
    }
}
