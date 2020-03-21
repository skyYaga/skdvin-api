package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;

import java.util.List;

public class JumpdayDTO extends AbstractJumpday {

    private List<TandemmasterDTO> tandemmaster;

    private List<VideoflyerDTO> videoflyer;

    public List<TandemmasterDTO> getTandemmaster() {
        return tandemmaster;
    }

    public void setTandemmaster(List<TandemmasterDTO> tandemmaster) {
        this.tandemmaster = tandemmaster;
    }

    public List<VideoflyerDTO> getVideoflyer() {
        return videoflyer;
    }

    public void setVideoflyer(List<VideoflyerDTO> videoflyer) {
        this.videoflyer = videoflyer;
    }
}