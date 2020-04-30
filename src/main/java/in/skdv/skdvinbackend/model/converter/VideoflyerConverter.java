package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VideoflyerConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public VideoflyerDTO convertToDto(Videoflyer videoflyer) {
        if (videoflyer == null) {
            return null;
        }
        return modelMapper.map(videoflyer, VideoflyerDTO.class);
    }

    public List<VideoflyerDTO> convertToDto(List<Videoflyer> videoflyers) {
        if (videoflyers == null) {
            return Collections.emptyList();
        }
        List<VideoflyerDTO> videoflyerDTOList = new ArrayList<>();
        videoflyers.forEach(a -> videoflyerDTOList.add(this.convertToDto(a)));
        return videoflyerDTOList;
    }

    public Videoflyer convertToEntity(VideoflyerDTO videoflyerDTO) {
        if (videoflyerDTO == null) {
            return null;
        }
        return modelMapper.map(videoflyerDTO, Videoflyer.class);
    }

    public VideoflyerDetailsDTO convertToDetailsDto(Videoflyer videoflyer, Map<LocalDate, SimpleAssignment> assignments) {
        if (videoflyer == null) {
            return null;
        }
        VideoflyerDetailsDTO videoflyerDetailsDTO = modelMapper.map(videoflyer, VideoflyerDetailsDTO.class);
        videoflyerDetailsDTO.setAssignments(assignments);
        return videoflyerDetailsDTO;
    }
}
