package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;

public class TandemmasterDTO extends AbstractFlyer implements ITandemmaster {

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
