package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.Slot;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public abstract class AbstractJumpday {

    @NotNull
    private LocalDate date;

    private boolean jumping;

    private boolean freeTimes;

    private List<Slot> slots;

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

    @Override
    public String toString() {
        return "JumpdayDTO{" +
                "date=" + date +
                ", jumping=" + jumping +
                ", freeTimes=" + freeTimes +
                ", slots=" + slots +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
