package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.AssignmentDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(uses = {VideoflyerMapper.class, TandemmasterMapper.class})
public interface AssignmentMapper {


    SimpleAssignment tandemmasterToSimpleAssignment(Assignment<Tandemmaster> assignment);

    SimpleAssignment videoflyerToSimpleAssignment(Assignment<Videoflyer> assignment);

    @Mapping(target = ".", source = "simpleAssignment")
    @Mapping(target = "flyer", source = "flyer")
    Assignment<Tandemmaster> toAssignment(SimpleAssignment simpleAssignment, Tandemmaster flyer);

    @Mapping(target = ".", source = "simpleAssignment")
    @Mapping(target = "flyer", source = "flyer")
    Assignment<Videoflyer> toAssignment(SimpleAssignment simpleAssignment, Videoflyer flyer);

    AssignmentDTO<TandemmasterDTO> toTandemmasterAssignmentDTO(Assignment<Tandemmaster> assignment);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<AssignmentDTO<TandemmasterDTO>> toTandemmasterAssignmentDTO(List<Assignment<Tandemmaster>> assignments);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<AssignmentDTO<VideoflyerDTO>> toVideoflyerAssignmentDTO(List<Assignment<Videoflyer>> assignments);

    AssignmentDTO<VideoflyerDTO> toVideoflyerAssignmentDTO(Assignment<Videoflyer> assignment);
}
