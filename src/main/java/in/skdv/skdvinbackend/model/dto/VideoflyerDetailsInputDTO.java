package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractVideoflyer;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoflyerDetailsInputDTO extends AbstractVideoflyer {

    private Map<LocalDate, SimpleAssignment> assignments;

}
