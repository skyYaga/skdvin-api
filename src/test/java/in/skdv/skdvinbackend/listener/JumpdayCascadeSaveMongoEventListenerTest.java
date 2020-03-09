package in.skdv.skdvinbackend.listener;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import static org.mockito.Mockito.*;

public class JumpdayCascadeSaveMongoEventListenerTest {

    @Test
    public void testListener() {
        MongoOperations mongoOperations = mock(MongoOperations.class);
        JumpdayCascadeSaveMongoEventListener listener = new JumpdayCascadeSaveMongoEventListener(mongoOperations);

        Jumpday jumpday = ModelMockHelper.createJumpday();
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        jumpday.addAppointment(appointment);
        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(jumpday, "jumpday");

        listener.onBeforeConvert(event);

        verify(mongoOperations, times(1)).save(appointment);
    }
}
