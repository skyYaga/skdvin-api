package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsInputDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerInputDTO;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;
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
public interface VideoflyerMapper {

    VideoflyerDTO toDto(Videoflyer videoflyer);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<VideoflyerDTO> toDto(List<Videoflyer> videoflyers);

    Videoflyer toEntity(VideoflyerDTO videoflyerDTO);

    VideoflyerDetails toEntity(VideoflyerDetailsDTO videoflyerDetailsDTO);

    @Mapping(target = ".", source = "videoflyer")
    @Mapping(target = "assignments", source = "assignments")
    VideoflyerDetailsDTO toDetailsDto(Videoflyer videoflyer, Map<LocalDate, SimpleAssignment> assignments);

    @Mapping(target = ".", source = "videoflyer")
    @Mapping(target = "assignments", source = "assignments")
    VideoflyerDetails toDetails(Videoflyer videoflyer, Map<LocalDate, SimpleAssignment> assignments);

    Videoflyer fromDetails(VideoflyerDetails videoflyerDetails);

    @Mapping(target = ".", source = "input")
    @Mapping(target = "id", source = "id")
    VideoflyerDetails toEntity(String id, VideoflyerDetailsInputDTO input);

    @Mapping(target = ".", source = "input")
    @Mapping(target = "id", source = "id")
    Videoflyer toEntity(String id, VideoflyerInputDTO input);

    VideoflyerDetailsDTO toDto(VideoflyerDetails videoflyer);
}
