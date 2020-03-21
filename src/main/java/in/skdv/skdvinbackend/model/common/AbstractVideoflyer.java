package in.skdv.skdvinbackend.model.common;

public abstract class AbstractVideoflyer extends AbstractFlyer {

    private boolean picAndVid;

    public boolean isPicAndVid() {
        return picAndVid;
    }

    public void setPicAndVid(boolean picAndVid) {
        this.picAndVid = picAndVid;
    }
}
