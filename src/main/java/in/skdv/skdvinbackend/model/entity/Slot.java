package in.skdv.skdvinbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Slot {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private int tandemTotal;
    private int videoTotal;
    private int tandemBooked;
    private int videoBooked;
    private int tandemAvailable;
    private int videoAvailable;
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

    public int getVideoTotal() {
        return videoTotal;
    }

    public void setVideoTotal(int videoTotal) {
        this.videoTotal = videoTotal;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public int getTandemBooked() {
        return appointments.stream().mapToInt(Appointment::getTandem).sum();
    }

    public int getVideoBooked() {
        return appointments.stream().mapToInt(Appointment::getVideo).sum();
    }

    public int getTandemAvailable() {
        return getTandemTotal() - getTandemBooked();
    }

    public int getVideoAvailable() {
        return getVideoTotal() - getVideoBooked();
    }

    @Override
    public String toString() {
        return "Slot{" +
                "time=" + time +
                ", tandemTotal=" + tandemTotal +
                ", videoTotal=" + videoTotal +
                ", appointments=" + appointments +
                '}';
    }
}
