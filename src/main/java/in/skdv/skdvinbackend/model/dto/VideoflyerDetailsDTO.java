package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractVideoflyer;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoflyerDetailsDTO extends AbstractVideoflyer {

    private String id;
    private Map<LocalDate, SimpleAssignment> assignments;

}
