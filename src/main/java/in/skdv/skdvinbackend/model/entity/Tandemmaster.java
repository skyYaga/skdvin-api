package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractTandemmaster;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Tandemmaster extends AbstractTandemmaster {

    @Id
    private String id;

    public Tandemmaster() {
        super();
    }

    public Tandemmaster(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
