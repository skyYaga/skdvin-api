package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;
import java.util.Optional;

public class Jumpday extends AbstractJumpday {

    @Id
    private ObjectId objectId;

    @DBRef
    private List<Tandemmaster> tandemmaster;

    @DBRef
    private List<Videoflyer> videoflyer;

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

    public List<Tandemmaster> getTandemmaster() {
        return tandemmaster;
    }

    public void setTandemmaster(List<Tandemmaster> tandemmaster) {
        this.tandemmaster = tandemmaster;
    }

    public List<Videoflyer> getVideoflyer() {
        return videoflyer;
    }

    public void setVideoflyer(List<Videoflyer> videoflyer) {
        this.videoflyer = videoflyer;
    }
}
