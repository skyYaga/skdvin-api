package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.waiver.AbstractWaiver;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WaiverInputDTO extends AbstractWaiver {

    private String waiverText;

}
