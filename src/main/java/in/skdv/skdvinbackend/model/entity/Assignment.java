package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Assignment<T extends AbstractFlyer> extends SimpleAssignment {

    @DBRef
    @Id
    private T flyer;

    public T getFlyer() {
        return flyer;
    }

    public void setFlyer(T flyer) {
        this.flyer = flyer;
    }

}
