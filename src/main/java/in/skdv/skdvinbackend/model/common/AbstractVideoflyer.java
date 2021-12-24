package in.skdv.skdvinbackend.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractVideoflyer extends AbstractFlyer {

    private boolean picAndVid;

}
