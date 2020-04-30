package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TandemmasterConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public TandemmasterDTO convertToDto(Tandemmaster tandemmaster) {
        if (tandemmaster == null) {
            return null;
        }
        return modelMapper.map(tandemmaster, TandemmasterDTO.class);
    }

    public List<TandemmasterDTO> convertToDto(List<Tandemmaster> tandemmasters) {
        if (tandemmasters == null) {
            return Collections.emptyList();
        }
        List<TandemmasterDTO> tandemmasterDTOList = new ArrayList<>();
        tandemmasters.forEach(a -> tandemmasterDTOList.add(this.convertToDto(a)));
        return tandemmasterDTOList;
    }

    public Tandemmaster convertToEntity(TandemmasterDTO tandemmasterDTO) {
        if (tandemmasterDTO == null) {
            return null;
        }
        return modelMapper.map(tandemmasterDTO, Tandemmaster.class);
    }

    public TandemmasterDetailsDTO convertToDetailsDto(Tandemmaster tandemmaster, Map<LocalDate, SimpleAssignment> assignments) {
        if (tandemmaster == null) {
            return null;
        }
        TandemmasterDetailsDTO tandemmasterDetailsDTO = modelMapper.map(tandemmaster, TandemmasterDetailsDTO.class);
        tandemmasterDetailsDTO.setAssignments(assignments);
        return tandemmasterDetailsDTO;
    }
}
