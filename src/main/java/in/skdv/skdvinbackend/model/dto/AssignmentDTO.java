package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;

public class AssignmentDTO<T extends AbstractFlyer> extends SimpleAssignment {

    private T flyer;

    public T getFlyer() {
        return flyer;
    }

    public void setFlyer(T flyer) {
        this.flyer = flyer;
    }

}
