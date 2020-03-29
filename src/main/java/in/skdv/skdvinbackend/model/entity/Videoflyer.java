package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.dto.IVideoflyer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Videoflyer extends AbstractFlyer implements IVideoflyer {

    @Id
    private String id;
    private boolean picAndVid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isPicAndVid() {
        return picAndVid;
    }

    @Override
    public void setPicAndVid(boolean picAndVid) {
        this.picAndVid = picAndVid;
    }
}
