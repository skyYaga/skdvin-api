package in.skdv.skdvinbackend.model.common;

import java.time.LocalDate;
import java.util.Map;

public abstract class AbstractDetailsFlyer extends AbstractFlyer {

    private String id;
    private Map<LocalDate, SimpleAssignment> assignments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<LocalDate, SimpleAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Map<LocalDate, SimpleAssignment> assignments) {
        this.assignments = assignments;
    }
}
