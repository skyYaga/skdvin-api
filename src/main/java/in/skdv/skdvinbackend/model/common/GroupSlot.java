package in.skdv.skdvinbackend.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class GroupSlot {

    private LocalDate date;
    private List<SimpleSlot> slots = new ArrayList<>();

    @JsonFormat(pattern = "HH:mm")
    public LocalTime getFirstTime() {
        return slots.stream().min(Comparator.comparing(SimpleSlot::getTime)).orElseThrow().getTime();
    }

    @JsonFormat(pattern = "HH:mm")
    public LocalTime getLastTime() {
        return slots.stream().max(Comparator.comparing(SimpleSlot::getTime)).orElseThrow().getTime();
    }

    public int getTimeCount() {
        return slots.size();
    }

    public int getTandemAvailable() {
        return slots.stream().mapToInt(SimpleSlot::getTandemAvailable).sum();
    }

    public int getPicOrVidAvailable() {
        return slots.stream().mapToInt(SimpleSlot::getPicOrVidAvailable).sum();
    }

    public int getPicAndVidAvailable() {
        return slots.stream().mapToInt(SimpleSlot::getPicAndVidAvailable).sum();
    }

    public int getHandcamAvailable() {
        return slots.stream().mapToInt(SimpleSlot::getHandcamAvailable).sum();
    }
}
