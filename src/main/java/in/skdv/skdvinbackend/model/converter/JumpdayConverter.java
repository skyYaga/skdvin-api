package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JumpdayConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public JumpdayDTO convertToDto(Jumpday jumpday) {
        if (jumpday == null) {
            return null;
        }
        return modelMapper.map(jumpday, JumpdayDTO.class);
    }

    public List<JumpdayDTO> convertToDto(List<Jumpday> jumpdays) {
        if (jumpdays == null) {
            return Collections.emptyList();
        }
        List<JumpdayDTO> jumpdayDTOList = new ArrayList<>();
        jumpdays.forEach(a -> jumpdayDTOList.add(this.convertToDto(a)));
        return jumpdayDTOList;
    }

    public Jumpday convertToEntity(JumpdayDTO jumpdayDTO) {
        if (jumpdayDTO == null) {
            return null;
        }
        return modelMapper.map(jumpdayDTO, Jumpday.class);
    }
}
