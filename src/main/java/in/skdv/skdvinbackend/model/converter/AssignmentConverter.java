package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.AssignmentDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssignmentConverter {

    private final ModelMapper modelMapper = new ModelMapper();
    private final ModelMapper videoflyerMapper = new ModelMapper();
    private final ModelMapper tandemmasterMapper = new ModelMapper();

    public AssignmentConverter() {
        initTandemmasterMapper();
        initVideoflyerMapper();
    }

    public <T extends AbstractFlyer> SimpleAssignment convertToSimpleAssignment(Assignment<T> assignment) {
        if (assignment == null) {
            return null;
        }
        return modelMapper.map(assignment, SimpleAssignment.class);
    }

    public <T extends AbstractFlyer> Assignment<T> convertToAssignment(SimpleAssignment simpleAssignment, T flyer) {
        if (simpleAssignment == null) {
            return null;
        }
        Assignment<T> assignment = modelMapper.map(simpleAssignment, Assignment.class);
        assignment.setFlyer(flyer);
        return assignment;
    }

    public List<AssignmentDTO<TandemmasterDTO>> convertToTandemmasterAssignmentDTO(List<Assignment<Tandemmaster>> assignments) {
        if (assignments == null) {
            return Collections.emptyList();
        }
        List<AssignmentDTO<TandemmasterDTO>> assignmentDTOList = new ArrayList<>();
        assignments.forEach(a -> assignmentDTOList.add(this.convertToTandemmasterAssignmentDTO(a)));
        return assignmentDTOList;
    }

    public AssignmentDTO<TandemmasterDTO> convertToTandemmasterAssignmentDTO(Assignment<Tandemmaster> assignment) {
        if (assignment == null) {
            return null;
        }

        return tandemmasterMapper.map(assignment, AssignmentDTO.class);
    }

    public List<AssignmentDTO<VideoflyerDTO>> convertToVideoflyerAssignmentDTO(List<Assignment<Videoflyer>> assignments) {
        if (assignments == null) {
            return Collections.emptyList();
        }
        List<AssignmentDTO<VideoflyerDTO>> assignmentDTOList = new ArrayList<>();
        assignments.forEach(a -> assignmentDTOList.add(this.convertToVideoflyerAssignmentDTO(a)));
        return assignmentDTOList;
    }

    public AssignmentDTO<VideoflyerDTO> convertToVideoflyerAssignmentDTO(Assignment<Videoflyer> assignment) {
        if (assignment == null) {
            return null;
        }

        return videoflyerMapper.map(assignment, AssignmentDTO.class);
    }

    private void initTandemmasterMapper() {
        TandemmasterConverter tandemmasterConverter = new TandemmasterConverter();
        Converter<Tandemmaster, TandemmasterDTO> tandemmasterMappingConverter =
                context -> tandemmasterConverter.convertToDto(context.getSource());

        PropertyMap<Assignment<Tandemmaster>, AssignmentDTO<TandemmasterDTO>> propertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(tandemmasterMappingConverter).map(source.getFlyer()).setFlyer(null);
            }
        };
        tandemmasterMapper.addMappings(propertyMap);
    }

    private void initVideoflyerMapper() {
        VideoflyerConverter videoflyerConverter = new VideoflyerConverter();
        Converter<Videoflyer, VideoflyerDTO> videflyerMappingConverter =
                context -> videoflyerConverter.convertToDto(context.getSource());

        PropertyMap<Assignment<Videoflyer>, AssignmentDTO<VideoflyerDTO>> propertyMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(videflyerMappingConverter).map(source.getFlyer()).setFlyer(null);
            }
        };
        videoflyerMapper.addMappings(propertyMap);
    }
}
