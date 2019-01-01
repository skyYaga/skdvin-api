package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.entity.Slot;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class JumpdayDTO {

    @NotNull
    @NotEmpty
    private LocalDate date;

    @NotNull
    @NotEmpty
    private boolean jumping;

    private boolean freeTimes;

    private List<Slot> slots;
    private List<String> tandemmaster;
    private List<String> videoflyer;

    @NotNull
    private String clientId;

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
        return "JumpdayDTO{" +
                "date=" + date +
                ", jumping=" + jumping +
                ", freeTimes=" + freeTimes +
                ", slots=" + slots +
                ", tandemmaster=" + tandemmaster +
                ", videoflyer=" + videoflyer +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}