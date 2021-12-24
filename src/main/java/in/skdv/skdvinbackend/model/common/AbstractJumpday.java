package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.Slot;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public abstract class AbstractJumpday {

    @NotNull
    private LocalDate date;

    private boolean jumping;

    private boolean freeTimes;

    private List<Slot> slots;

    private String clientId;
}
