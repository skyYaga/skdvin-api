package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractJumpday;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JumpdayDTO extends AbstractJumpday {

    private List<AssignmentDTO<TandemmasterDTO>> tandemmaster;

    private List<AssignmentDTO<VideoflyerDTO>> videoflyer;
}