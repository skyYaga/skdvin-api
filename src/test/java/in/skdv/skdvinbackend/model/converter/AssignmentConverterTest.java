package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.AssignmentDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class AssignmentConverterTest {

    AssignmentConverter converter = new AssignmentConverter();

    @Test
    public void testConvertTandemmasterAssignment() {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        assignment.setFlyer(tandemmaster);

        AssignmentDTO<TandemmasterDTO> assignmentDTO = converter.convertToTandemmasterAssignmentDTO(assignment);

        Assert.assertTrue(assignmentDTO.getFlyer() instanceof TandemmasterDTO);
        Assert.assertEquals(tandemmaster.getFirstName(), assignmentDTO.getFlyer().getFirstName());
    }

    @Test
    public void testConvertTandemmasterAssignmentList() {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        assignment.setFlyer(tandemmaster);
        List<Assignment<Tandemmaster>> assignments = Collections.singletonList(assignment);

        List<AssignmentDTO<TandemmasterDTO>> assignmentDTOs = converter.convertToTandemmasterAssignmentDTO(assignments);

        Assert.assertTrue(assignmentDTOs.get(0).getFlyer() instanceof TandemmasterDTO);
        Assert.assertEquals(tandemmaster.getFirstName(), assignmentDTOs.get(0).getFlyer().getFirstName());
    }

    @Test
    public void testConvertVideoflyerAssignment() {
        Assignment<Videoflyer> assignment = new Assignment<>();
        Videoflyer tandemmaster = ModelMockHelper.createVideoflyer();
        assignment.setFlyer(tandemmaster);

        AssignmentDTO<VideoflyerDTO> assignmentDTO = converter.convertToVideoflyerAssignmentDTO(assignment);

        Assert.assertTrue(assignmentDTO.getFlyer() instanceof VideoflyerDTO);
        Assert.assertEquals(tandemmaster.getFirstName(), assignmentDTO.getFlyer().getFirstName());
    }

    @Test
    public void testConvertVideoflyerAssignmentList() {
        Assignment<Videoflyer> assignment = new Assignment<>();
        Videoflyer tandemmaster = ModelMockHelper.createVideoflyer();
        assignment.setFlyer(tandemmaster);
        List<Assignment<Videoflyer>> assignments = Collections.singletonList(assignment);

        List<AssignmentDTO<VideoflyerDTO>> assignmentDTOs = converter.convertToVideoflyerAssignmentDTO(assignments);

        Assert.assertTrue(assignmentDTOs.get(0).getFlyer() instanceof VideoflyerDTO);
        Assert.assertEquals(tandemmaster.getFirstName(), assignmentDTOs.get(0).getFlyer().getFirstName());
    }
}
