package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractTandemmaster;

public class TandemmasterDTO extends AbstractTandemmaster {

    private String id;

    public TandemmasterDTO() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
