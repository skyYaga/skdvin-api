package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Jumpday extends AbstractJumpday {

    @Id
    private ObjectId objectId;


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
