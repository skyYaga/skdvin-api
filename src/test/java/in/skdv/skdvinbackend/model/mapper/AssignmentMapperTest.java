package in.skdv.skdvinbackend.model.mapper;

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

class AssignmentMapperTest {

    AssignmentMapper mapper = new AssignmentMapperImpl(new VideoflyerMapperImpl(), new TandemmasterMapperImpl());

    @Test
    void testTandemmasterToSimpleAssignment() {
        SimpleAssignment simpleAssignment = mapper.tandemmasterToSimpleAssignment(ModelMockHelper.createAssignment(new Tandemmaster()));
        assertNotNull(simpleAssignment);
        assertTrue(simpleAssignment.isAssigned());
    }

    @Test
    void testTandemmasterToSimpleAssignment_Null() {
        assertNull(mapper.tandemmasterToSimpleAssignment(null));
    }

    @Test
    void testVideoflyerToSimpleAssignment() {
        SimpleAssignment simpleAssignment = mapper.videoflyerToSimpleAssignment(ModelMockHelper.createAssignment(new Videoflyer()));
        assertNotNull(simpleAssignment);
        assertTrue(simpleAssignment.isAssigned());
    }

    @Test
    void testVideoflyerToSimpleAssignment_Null() {
        assertNull(mapper.videoflyerToSimpleAssignment(null));
    }

    @Test
    void testToAssignment_Tandemmaster() {
        SimpleAssignment simpleAssignment = new SimpleAssignment(true);
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();

        Assignment<Tandemmaster> assignment = mapper.toAssignment(simpleAssignment, tandemmaster);

        assertNotNull(assignment);
        assertEquals(tandemmaster.getFirstName(), assignment.getFlyer().getFirstName());
        assertTrue(assignment.isAssigned());
    }

    @Test
    void testToAssignment_Videoflyer() {
        SimpleAssignment simpleAssignment = new SimpleAssignment(true);
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();

        Assignment<Videoflyer> assignment = mapper.toAssignment(simpleAssignment, videoflyer);

        assertNotNull(assignment);
        assertEquals(videoflyer.getFirstName(), assignment.getFlyer().getFirstName());
        assertTrue(assignment.isAssigned());
    }

    @Test
    void testtoAssignment_Tandemmaster_Null() {
        assertNull(mapper.toAssignment(null, (Tandemmaster) null));
    }

    @Test
    void testtoAssignment_Videoflyer_Null() {
        assertNull(mapper.toAssignment(null, (Videoflyer) null));
    }

    @Test
    void testtandemmasterAssignment() {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        assignment.setFlyer(tandemmaster);

        AssignmentDTO<TandemmasterDTO> assignmentDTO = mapper.toTandemmasterAssignmentDTO(assignment);

        assertTrue(assignmentDTO.getFlyer() instanceof TandemmasterDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTO.getFlyer().getFirstName());
    }

    @Test
    void testtandemmasterAssignment_Null() {
        assertNull(mapper.toTandemmasterAssignmentDTO((Assignment<Tandemmaster>) null));
    }

    @Test
    void testtandemmasterAssignmentList() {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        assignment.setFlyer(tandemmaster);
        List<Assignment<Tandemmaster>> assignments = Collections.singletonList(assignment);

        List<AssignmentDTO<TandemmasterDTO>> assignmentDTOs = mapper.toTandemmasterAssignmentDTO(assignments);

        assertTrue(assignmentDTOs.get(0).getFlyer() instanceof TandemmasterDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTOs.get(0).getFlyer().getFirstName());
    }

    @Test
    void testtandemmasterAssignmentList_Null() {
        assertEquals(0, mapper.toTandemmasterAssignmentDTO((List<Assignment<Tandemmaster>>) null).size());
    }

    @Test
    void testConvertVideoflyerAssignment() {
        Assignment<Videoflyer> assignment = new Assignment<>();
        Videoflyer tandemmaster = ModelMockHelper.createVideoflyer();
        assignment.setFlyer(tandemmaster);

        AssignmentDTO<VideoflyerDTO> assignmentDTO = mapper.toVideoflyerAssignmentDTO(assignment);

        assertTrue(assignmentDTO.getFlyer() instanceof VideoflyerDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTO.getFlyer().getFirstName());
    }

    @Test
    void testConvertVideoflyerAssignment_Null() {
        assertNull(mapper.toVideoflyerAssignmentDTO((Assignment<Videoflyer>) null));
    }

    @Test
    void testConvertVideoflyerAssignmentList() {
        Assignment<Videoflyer> assignment = new Assignment<>();
        Videoflyer tandemmaster = ModelMockHelper.createVideoflyer();
        assignment.setFlyer(tandemmaster);
        List<Assignment<Videoflyer>> assignments = Collections.singletonList(assignment);

        List<AssignmentDTO<VideoflyerDTO>> assignmentDTOs = mapper.toVideoflyerAssignmentDTO(assignments);

        assertTrue(assignmentDTOs.get(0).getFlyer() instanceof VideoflyerDTO);
        assertEquals(tandemmaster.getFirstName(), assignmentDTOs.get(0).getFlyer().getFirstName());
    }

    @Test
    void testConvertVideoflyerAssignmentList_Null() {
        assertEquals(0, mapper.toVideoflyerAssignmentDTO((List<Assignment<Videoflyer>>) null).size());
    }
}
