package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.dto.ITandemmaster;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Tandemmaster extends AbstractFlyer implements ITandemmaster {

    @Id
    private String id;
    private boolean handcam;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isHandcam() {
        return handcam;
    }

    @Override
    public void setHandcam(boolean handcam) {
        this.handcam = handcam;
    }
}
