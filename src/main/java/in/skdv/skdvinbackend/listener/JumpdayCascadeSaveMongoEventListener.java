package in.skdv.skdvinbackend.listener;

import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

public class JumpdayCascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {

    private MongoOperations mongoOperations;

    @Autowired
    public JumpdayCascadeSaveMongoEventListener(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        if (source instanceof Jumpday) {
            ((Jumpday) source).getSlots().forEach(slot -> {
                if (slot.getAppointments() != null) {
                    slot.getAppointments().forEach(appointment -> mongoOperations.save(appointment));
                }
            });
        }
    }
}
