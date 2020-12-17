package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.AssignmentDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentConverterTest {

    AssignmentConverter converter = new AssignmentConverter();

    @Test
    void testConvertToSimpleAssignment() {
        SimpleAssignment simpleAssignment = converter.convertToSimpleAssignment(ModelMockHelper.createAssignment(new Tandemmaster()));
        assertNotNull(simpleAssignment);
        assertTrue(simpleAssignment.isAssigned());
    }

    @Test
    void testConvertToSimpleAssignment_Null() {
        assertNull(converter.convertToSimpleAssignment(null));
    }

    @Test
    void testConvertToAssignment() {
        SimpleAssignment simpleAssignment = new SimpleAssignment(true);
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();

        Assignment assignment = converter.convertToAssignment(simpleAssignment, tandemmaster);

        assertNotNull(assignment);
        assertEquals(tandemmaster.getFirstName(), assignment.getFlyer().getFirstName());
        assertTrue(assignment.isAssigned());
    }

    @Test
    void testConvertToAssignment_Null() {
        assertNull(converter.convertToAssignment(null, null));
    }

    @Test
    void testConvertTandemmasterAssignment() {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        assignment.setFlyer(tandemmaster);

        AssignmentDTO<TandemmasterDTO> assignmentDTO = converter.convertToTandemmasterAssignmentDTO(assignment);

        assertTrue(assignmentDTO.getFlyer() instanceof TandemmasterDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTO.getFlyer().getFirstName());
    }

    @Test
    void testConvertTandemmasterAssignment_Null() {
        assertNull(converter.convertToTandemmasterAssignmentDTO((Assignment<Tandemmaster>) null));
    }

    @Test
    void testConvertTandemmasterAssignmentList() {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        assignment.setFlyer(tandemmaster);
        List<Assignment<Tandemmaster>> assignments = Collections.singletonList(assignment);

        List<AssignmentDTO<TandemmasterDTO>> assignmentDTOs = converter.convertToTandemmasterAssignmentDTO(assignments);

        assertTrue(assignmentDTOs.get(0).getFlyer() instanceof TandemmasterDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTOs.get(0).getFlyer().getFirstName());
    }

    @Test
    void testConvertTandemmasterAssignmentList_Null() {
        assertEquals(0, converter.convertToTandemmasterAssignmentDTO((List<Assignment<Tandemmaster>>) null).size());
    }

    @Test
    void testConvertVideoflyerAssignment() {
        Assignment<Videoflyer> assignment = new Assignment<>();
        Videoflyer tandemmaster = ModelMockHelper.createVideoflyer();
        assignment.setFlyer(tandemmaster);

        AssignmentDTO<VideoflyerDTO> assignmentDTO = converter.convertToVideoflyerAssignmentDTO(assignment);

        assertTrue(assignmentDTO.getFlyer() instanceof VideoflyerDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTO.getFlyer().getFirstName());
    }

    @Test
    void testConvertVideoflyerAssignment_Null() {
        assertNull(converter.convertToVideoflyerAssignmentDTO((Assignment<Videoflyer>) null));
    }

    @Test
    void testConvertVideoflyerAssignmentList() {
        Assignment<Videoflyer> assignment = new Assignment<>();
        Videoflyer tandemmaster = ModelMockHelper.createVideoflyer();
        assignment.setFlyer(tandemmaster);
        List<Assignment<Videoflyer>> assignments = Collections.singletonList(assignment);

        List<AssignmentDTO<VideoflyerDTO>> assignmentDTOs = converter.convertToVideoflyerAssignmentDTO(assignments);

        assertTrue(assignmentDTOs.get(0).getFlyer() instanceof VideoflyerDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTOs.get(0).getFlyer().getFirstName());
    }

    @Test
    void testConvertVideoflyerAssignmentList_Null() {
        assertEquals(0, converter.convertToVideoflyerAssignmentDTO((List<Assignment<Videoflyer>>) null).size());
    }
}
