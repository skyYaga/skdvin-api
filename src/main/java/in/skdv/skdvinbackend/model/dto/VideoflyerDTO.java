package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;

public class VideoflyerDTO extends AbstractFlyer implements IVideoflyer {

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
