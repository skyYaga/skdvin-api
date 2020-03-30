package in.skdv.skdvinbackend.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class SimpleAssignment {

    private boolean assigned = false;
    private boolean allday = true;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime from;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime to;

    public SimpleAssignment() {
    }

    public SimpleAssignment(boolean assigned) {
        this.assigned = assigned;
    }

    public SimpleAssignment(boolean assigned, boolean allday, LocalTime from, LocalTime to) {
        this.assigned = assigned;
        this.allday = allday;
        this.from = from;
        this.to = to;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public boolean isAllday() {
        return allday;
    }

    public void setAllday(boolean allday) {
        this.allday = allday;
    }

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        this.to = to;
    }
}
