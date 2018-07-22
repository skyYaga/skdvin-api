package in.skdv.skdvinbackend.model;

import org.springframework.data.annotation.Id;

import java.time.LocalTime;
import java.util.List;

public class Slot {

    @Id
    private LocalTime time;
    private boolean operating;
    private int tandemTotal;
    private int videoTotal;
    private List<Appointment> appointments;

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isOperating() {
        return operating;
    }

    public void setOperating(boolean operating) {
        this.operating = operating;
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

    @Override
    public String toString() {
        return "Slot{" +
                "time=" + time +
                ", operating=" + operating +
                ", tandemTotal=" + tandemTotal +
                ", videoTotal=" + videoTotal +
                ", appointments=" + appointments +
                '}';
    }
}
