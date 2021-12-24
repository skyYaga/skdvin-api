package in.skdv.skdvinbackend.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractTandemmaster extends AbstractFlyer {

    private boolean handcam;

}
