package in.skdv.skdvinbackend.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

public class Jumpday {

    @Id
    public LocalDate date;
    public boolean jumping;
    public boolean freeTimes;
    public List<Slot> slots;
    public List<String> tandemmaster;
    public List<String> videoflyer;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isFreeTimes() {
        return freeTimes;
    }

    public void setFreeTimes(boolean freeTimes) {
        this.freeTimes = freeTimes;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public List<String> getTandemmaster() {
        return tandemmaster;
    }

    public void setTandemmaster(List<String> tandemmaster) {
        this.tandemmaster = tandemmaster;
    }

    public List<String> getVideoflyer() {
        return videoflyer;
    }

    public void setVideoflyer(List<String> videoflyer) {
        this.videoflyer = videoflyer;
    }

    @Override
    public String toString() {
        return "Jumpday{" +
                "date=" + date +
                ", jumping=" + jumping +
                ", freeTimes=" + freeTimes +
                ", slots=" + slots +
                ", tandemmaster=" + tandemmaster +
                ", videoflyer=" + videoflyer +
                '}';
    }
}
