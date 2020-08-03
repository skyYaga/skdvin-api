package in.skdv.skdvinbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.skdv.skdvinbackend.model.common.SlotQuery;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Slot {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private int tandemTotal;
    private int picOrVidTotal;
    private int picAndVidTotal;
    private int handcamTotal;

    private List<Appointment> appointments = new ArrayList<>();


    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getTandemTotal() {
        return tandemTotal;
    }

    public void setTandemTotal(int tandemTotal) {
        this.tandemTotal = tandemTotal;
    }

    public int getPicOrVidTotal() {
        return picOrVidTotal;
    }

    public void setPicOrVidTotal(int picOrVidTotal) {
        this.picOrVidTotal = picOrVidTotal;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public int getTandemBooked() {
        return appointments.stream().filter(Objects::nonNull).mapToInt(Appointment::getTandem).sum();
    }

    public int getPicOrVidBooked() {
        return appointments.stream().filter(Objects::nonNull).mapToInt(Appointment::getPicOrVid).sum();
    }

    public int getPicAndVidBooked() {
        return appointments.stream().filter(Objects::nonNull).mapToInt(Appointment::getPicAndVid).sum();
    }

    public int getHandcamBooked() {
        return appointments.stream().filter(Objects::nonNull).mapToInt(Appointment::getHandcam).sum();
    }

    public int getTandemAvailable() {
        return getTandemTotal() - getTandemBooked();
    }

    public int getPicOrVidAvailable() {
        return getPicOrVidTotal() - getPicOrVidBooked() - getPicAndVidBooked();
    }

    public int getPicAndVidAvailable() {
        int picAndVidAvailable = getPicAndVidTotal() - getPicAndVidBooked();
        return Math.min(picAndVidAvailable, getPicOrVidAvailable());
    }

    public int getHandcamAvailable() {
        return getHandcamTotal() - getHandcamBooked();
    }

    @Override
    public String toString() {
        return "Slot{" +
                "time=" + time +
                ", tandemTotal=" + tandemTotal +
                ", picOrVidTotal=" + picOrVidTotal +
                ", picAndVidTotal=" + picAndVidTotal +
                ", handcamTotal=" + handcamTotal +
                ", appointments=" + appointments +
                '}';
    }

    public int getPicAndVidTotal() {
        return picAndVidTotal;
    }

    public void setPicAndVidTotal(int picAndVidTotal) {
        this.picAndVidTotal = picAndVidTotal;
    }

    public int getHandcamTotal() {
        return handcamTotal;
    }

    public void setHandcamTotal(int handcamTotal) {
        this.handcamTotal = handcamTotal;
    }

    public boolean isValidForQuery(SlotQuery slotQuery) {
        return slotQuery.getTandem() <= getTandemAvailable() &&
                slotQuery.getPicAndVid() <= getPicAndVidAvailable() &&
                slotQuery.getPicOrVid() <= getPicOrVidAvailable() &&
                slotQuery.getHandcam() <= getHandcamAvailable() &&
                enoughCombinedSlotsAvailable(slotQuery);
    }

    private boolean enoughCombinedSlotsAvailable(SlotQuery slotQuery) {
        return slotQuery.getPicAndVid() + slotQuery.getPicOrVid() <= getPicOrVidAvailable();
    }
}
