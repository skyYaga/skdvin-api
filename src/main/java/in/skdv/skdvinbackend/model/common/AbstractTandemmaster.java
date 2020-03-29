package in.skdv.skdvinbackend.model.common;

public abstract class AbstractTandemmaster extends AbstractFlyer {

    private boolean handcam;

    public boolean isHandcam() {
        return handcam;
    }

    public void setHandcam(boolean handcam) {
        this.handcam = handcam;
    }
}
