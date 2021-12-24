package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractTandemmaster;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class TandemmasterDetails extends AbstractTandemmaster {

    private String id;
    private Map<LocalDate, SimpleAssignment> assignments;

}
