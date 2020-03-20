package in.skdv.skdvinbackend.model.dto;

import java.time.LocalDate;
import java.util.Map;

public class TandemmasterDetailsDTO extends TandemmasterDTO {

    private Map<LocalDate, Boolean> assignments;

    public Map<LocalDate, Boolean> getAssignments() {
        return assignments;
    }

    public void setAssignments(Map<LocalDate, Boolean> assignments) {
        this.assignments = assignments;
    }
}
