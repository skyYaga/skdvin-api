package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        List<TandemmasterDTO> TandemmasterDTOList = new ArrayList<>();
        tandemmasters.forEach(a -> TandemmasterDTOList.add(this.convertToDto(a)));
        return TandemmasterDTOList;
    }

    public Tandemmaster convertToEntity(TandemmasterDTO tandemmasterDTO) {
        if (tandemmasterDTO == null) {
            return null;
        }
        return modelMapper.map(tandemmasterDTO, Tandemmaster.class);
    }
}
