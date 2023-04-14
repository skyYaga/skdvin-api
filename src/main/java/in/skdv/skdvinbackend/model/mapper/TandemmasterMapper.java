package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsInputDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterInputDTO;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.TandemmasterDetails;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface TandemmasterMapper {

    TandemmasterDTO toDto(Tandemmaster tandemmaster);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<TandemmasterDTO> toDto(List<Tandemmaster> tandemmasters);

    Tandemmaster toEntity(TandemmasterDTO tandemmasterDTO);

    TandemmasterDetails toEntity(TandemmasterDetailsDTO tandemmasterDetailsDTO);

    @Mapping(target = ".", source = "tandemmaster")
    @Mapping(target = "assignments", source = "assignments")
    TandemmasterDetailsDTO toDetailsDto(Tandemmaster tandemmaster, Map<LocalDate, SimpleAssignment> assignments);

    @Mapping(target = ".", source = "tandemmaster")
    @Mapping(target = "assignments", source = "assignments")
    TandemmasterDetails toDetails(Tandemmaster tandemmaster, Map<LocalDate, SimpleAssignment> assignments);

    Tandemmaster fromDetails(TandemmasterDetails tandemmasterDetails);

    @Mapping(target = ".", source = "input")
    @Mapping(target = "id", source = "id")
    TandemmasterDetails toEntity(String id, TandemmasterDetailsInputDTO input);

    @Mapping(target = ".", source = "input")
    @Mapping(target = "id", source = "id")
    Tandemmaster toEntity(String id, TandemmasterInputDTO input);

    TandemmasterDetailsDTO toDto(TandemmasterDetails tandemmaster);
}
