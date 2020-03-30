package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@Document
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

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public List<Assignment<Tandemmaster>> getTandemmaster() {
        return tandemmaster;
    }

    public void setTandemmaster(List<Assignment<Tandemmaster>> tandemmaster) {
        this.tandemmaster = tandemmaster;
    }

    public List<Assignment<Videoflyer>> getVideoflyer() {
        return videoflyer;
    }

    public void setVideoflyer(List<Assignment<Videoflyer>> videoflyer) {
        this.videoflyer = videoflyer;
    }
}
