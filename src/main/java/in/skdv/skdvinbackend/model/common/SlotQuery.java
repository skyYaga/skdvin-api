package in.skdv.skdvinbackend.model.common;

public class SlotQuery {

    private int tandem;
    private int picOrVid;
    private int picAndVid;
    private int handcam;

    public SlotQuery(int tandem, int picOrVid, int picAndVid, int handcam) {
        this.tandem = tandem;
        this.picOrVid = picOrVid;
        this.picAndVid = picAndVid;
        this.handcam = handcam;
    }

    public int getTandem() {
        return tandem;
    }

    public int getPicOrVid() {
        return picOrVid;
    }

    public int getPicAndVid() {
        return picAndVid;
    }

    public int getHandcam() {
        return handcam;
    }

    public boolean isValid() {
        return tandem >= picOrVid + picAndVid + handcam;
    }
}
