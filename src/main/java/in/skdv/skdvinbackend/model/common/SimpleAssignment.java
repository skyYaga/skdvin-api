package in.skdv.skdvinbackend.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAssignment {

    private boolean assigned = false;
    private boolean allday = true;
    private String note;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime from;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime to;

    public SimpleAssignment(boolean assigned) {
        this.assigned = assigned;
    }

}
