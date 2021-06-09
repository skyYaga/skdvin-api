package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaiverConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public WaiverDTO convertToDto(Waiver waiver) {
        if (waiver == null) {
            return null;
        }
        return modelMapper.map(waiver, WaiverDTO.class);
    }

    public List<WaiverDTO> convertToDto(List<Waiver> waivers) {
        if (waivers == null) {
            return Collections.emptyList();
        }
        List<WaiverDTO> waiverDTOList = new ArrayList<>();
        waivers.forEach(a -> waiverDTOList.add(this.convertToDto(a)));
        return waiverDTOList;
    }

    public Waiver convertToEntity(WaiverDTO waiverDTO) {
        if (waiverDTO == null) {
            return null;
        }
        return modelMapper.map(waiverDTO, Waiver.class);
    }

}
