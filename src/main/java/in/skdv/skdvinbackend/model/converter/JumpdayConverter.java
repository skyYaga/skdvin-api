package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.AssignmentDTO;
import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JumpdayConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public JumpdayConverter() {
        AssignmentConverter assignmentConverter = new AssignmentConverter();
        Converter<List<Assignment<Tandemmaster>>, List<AssignmentDTO<TandemmasterDTO>>> tandemmasterConverter =
                context -> assignmentConverter.convertToTandemmasterAssignmentDTO(context.getSource());
        Converter<List<Assignment<Videoflyer>>, List<AssignmentDTO<VideoflyerDTO>>> videoflyerConverter =
                context -> assignmentConverter.convertToVideoflyerAssignmentDTO(context.getSource());

        modelMapper.addConverter(tandemmasterConverter);
        modelMapper.addConverter(videoflyerConverter);
        modelMapper.addMappings(new PropertyMap<Jumpday, JumpdayDTO>() {
            @Override
            protected void configure() {
                using(tandemmasterConverter).map(source.getTandemmaster()).setTandemmaster(null);
                using(videoflyerConverter).map(source.getVideoflyer()).setVideoflyer(null);
            }
        });
    }

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
