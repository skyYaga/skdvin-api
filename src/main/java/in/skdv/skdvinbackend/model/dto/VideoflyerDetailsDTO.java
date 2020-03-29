package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractVideoflyer;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;

import java.time.LocalDate;
import java.util.Map;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class VideoflyerDetailsDTO extends AbstractVideoflyer {

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
