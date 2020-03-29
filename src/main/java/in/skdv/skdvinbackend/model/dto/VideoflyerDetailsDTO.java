package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractDetailsFlyer;

public class VideoflyerDetailsDTO extends AbstractDetailsFlyer implements IVideoflyer {

    private boolean picAndVid;

    @Override
    public boolean isPicAndVid() {
        return picAndVid;
    }

    @Override
    public void setPicAndVid(boolean picAndVid) {
        this.picAndVid = picAndVid;
    }
}
