package in.skdv.skdvinbackend.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class FreeSlot {

    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private List<LocalTime> times;

    public FreeSlot(LocalDate date, List<LocalTime> times) {
        this.date = date;
        this.times = times;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<LocalTime> getTimes() {
        return times;
    }
}
