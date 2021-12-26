package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsInputDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerInputDTO;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;
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

    public VideoflyerDetails convertToEntity(VideoflyerDetailsDTO videoflyerDetailsDTO) {
        if (videoflyerDetailsDTO == null) {
            return null;
        }
        return modelMapper.map(videoflyerDetailsDTO, VideoflyerDetails.class);
    }

    public VideoflyerDetailsDTO convertToDetailsDto(Videoflyer videoflyer, Map<LocalDate, SimpleAssignment> assignments) {
        if (videoflyer == null) {
            return null;
        }
        VideoflyerDetailsDTO videoflyerDetailsDTO = modelMapper.map(videoflyer, VideoflyerDetailsDTO.class);
        videoflyerDetailsDTO.setAssignments(assignments);
        return videoflyerDetailsDTO;
    }

    public VideoflyerDetails convertToDetails(Videoflyer videoflyer, Map<LocalDate, SimpleAssignment> assignments) {
        if (videoflyer == null) {
            return null;
        }
        VideoflyerDetails videoflyerDetails = modelMapper.map(videoflyer, VideoflyerDetails.class);
        videoflyerDetails.setAssignments(assignments);
        return videoflyerDetails;
    }

    public Videoflyer convertFromDetails(VideoflyerDetails videoflyerDetails) {
        if (videoflyerDetails == null) {
            return null;
        }
        return modelMapper.map(videoflyerDetails, Videoflyer.class);
    }

    public VideoflyerDetails convertToEntity(String id, VideoflyerDetailsInputDTO input) {
        VideoflyerDetails details = modelMapper.map(input, VideoflyerDetails.class);
        details.setId(id);
        return details;
    }

    public Videoflyer convertToEntity(String id, VideoflyerInputDTO input) {
        Videoflyer details = modelMapper.map(input, Videoflyer.class);
        details.setId(id);
        return details;
    }

    public VideoflyerDetailsDTO convertToDto(VideoflyerDetails videoflyer) {
        return modelMapper.map(videoflyer, VideoflyerDetailsDTO.class);
    }

}
