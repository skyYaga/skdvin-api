package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Optional;

public class Jumpday extends AbstractJumpday {

    @Id
    private ObjectId objectId;

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

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Jumpday{" +
                "objectId=" + objectId +
                '}';
    }
}
