package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractDetailsFlyer;

public class TandemmasterDetailsDTO extends AbstractDetailsFlyer implements ITandemmaster {

    private boolean handcam;

    @Override
    public boolean isHandcam() {
        return handcam;
    }

    @Override
    public void setHandcam(boolean handcam) {
        this.handcam = handcam;
    }
}
