package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.waiver.AbstractWaiver;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaiverDTO extends AbstractWaiver {

    private String id;

    private String waiverText;

}
