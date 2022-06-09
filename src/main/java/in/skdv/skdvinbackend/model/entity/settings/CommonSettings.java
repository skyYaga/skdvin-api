package in.skdv.skdvinbackend.model.entity.settings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonSettings {

    private SelfAssignmentMode selfAssignmentMode;
    private boolean picAndVidEnabled;

}
