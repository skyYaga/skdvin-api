package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;

import java.util.List;

public class JumpdayDTO extends AbstractJumpday {

    private List<AssignmentDTO<TandemmasterDTO>> tandemmaster;

    private List<AssignmentDTO<VideoflyerDTO>> videoflyer;

    public List<AssignmentDTO<TandemmasterDTO>> getTandemmaster() {
        return tandemmaster;
    }

    public void setTandemmaster(List<AssignmentDTO<TandemmasterDTO>> tandemmaster) {
        this.tandemmaster = tandemmaster;
    }

    public List<AssignmentDTO<VideoflyerDTO>> getVideoflyer() {
        return videoflyer;
    }

    public void setVideoflyer(List<AssignmentDTO<VideoflyerDTO>> videoflyer) {
        this.videoflyer = videoflyer;
    }
}