package in.skdv.skdvinbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Slot {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private int tandemTotal;
    private int picOrVidTotal;
    private int picAndVidTotal;
    private int handcamTotal;

    private List<Appointment> appointments = new ArrayList<>();

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
