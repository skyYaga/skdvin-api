package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;

@Component
@Mapper(uses = {AssignmentMapper.class})
public interface JumpdayMapper {

    JumpdayDTO toDto(Jumpday jumpday);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<JumpdayDTO> toDto(List<Jumpday> jumpdays);

    @Mapping(target = ".", source = "jumpdayDTO")
    @Mapping(target = "timezone", source = "zoneId")
    Jumpday toEntity(JumpdayDTO jumpdayDTO, ZoneId zoneId);
}
