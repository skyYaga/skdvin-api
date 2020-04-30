package in.skdv.skdvinbackend.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.skdv.skdvinbackend.model.entity.Slot;

import java.time.LocalTime;

public class SimpleSlot {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private int tandemAvailable;
    private int picOrVidAvailable;
    private int picAndVidAvailable;
    private int handcamAvailable;

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getTandemAvailable() {
        return tandemAvailable;
    }

    public void setTandemAvailable(int tandemAvailable) {
        this.tandemAvailable = tandemAvailable;
    }

    public int getPicOrVidAvailable() {
        return picOrVidAvailable;
    }

    public void setPicOrVidAvailable(int picOrVidAvailable) {
        this.picOrVidAvailable = picOrVidAvailable;
    }

    public int getPicAndVidAvailable() {
        return picAndVidAvailable;
    }

    public void setPicAndVidAvailable(int picAndVidAvailable) {
        this.picAndVidAvailable = picAndVidAvailable;
    }

    public int getHandcamAvailable() {
        return handcamAvailable;
    }

    public void setHandcamAvailable(int handcamAvailable) {
        this.handcamAvailable = handcamAvailable;
    }

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
