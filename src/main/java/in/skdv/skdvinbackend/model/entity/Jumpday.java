package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Jumpday extends AbstractJumpday {

    @Id
    private ObjectId objectId;

    private List<Assignment<Tandemmaster>> tandemmaster;

    private List<Assignment<Videoflyer>> videoflyer;

    public boolean addAppointment(Appointment appointment) {
        Optional<Slot> slot = getSlotForAppointment(appointment);
        if (slot.isPresent()) {
            slot.get().getAppointments().add(appointment);
            return true;
        }
        return false;
    }

    public Optional<Slot> getSlotForAppointment(Appointment appointment) {
        return getSlots().stream().filter(s -> s.getTime().equals(appointment.getDate().toLocalTime())).findFirst();
    }

}
