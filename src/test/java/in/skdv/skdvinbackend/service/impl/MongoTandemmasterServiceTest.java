package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTandemmasterServiceTest extends AbstractSkdvinTest {

    @Autowired
    JumpdayRepository jumpdayRepository;

    @Autowired
    TandemmasterRepository tandemmasterRepository;

    @Autowired
    ITandemmasterService tandemmasterService;

    @Autowired
    IJumpdayService jumpdayService;

    @Before
    public void setup() {
        jumpdayRepository.deleteAll();
        tandemmasterRepository.deleteAll();
    }

    @Test
    public void testGetTandemmasterById() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        jumpday1.getTandemmaster().add(createAssignment(tandemmaster));
        jumpday2.getTandemmaster().add(createAssignment(tandemmaster));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        TandemmasterDetailsDTO tandemmasterDetails = tandemmasterService.getById(tandemmaster.getId());

        Assert.assertNotNull(tandemmasterDetails);
        Assert.assertEquals(2, tandemmasterDetails.getAssignments().size());
    }

    private Assignment<Tandemmaster> createAssignment(Tandemmaster tandemmaster) {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        assignment.setFlyer(tandemmaster);
        assignment.setAssigned(true);
        return assignment;
    }

    @Test
    public void testGetTandemmasterById_NotFound() {
        TandemmasterDetailsDTO tandemmasterDetails = tandemmasterService.getById("99999999999");
        Assert.assertNull(tandemmasterDetails);
    }


    @Test
    public void testAssignTandemmasterToJumpday() {
        assignTandemmaster(ModelMockHelper.createJumpday());
    }

    @Test
    public void testAssignTandemmasterToJumpday_AlreadyAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String tandemmasterId = assignTandemmaster(jumpday);

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                jumpday.getDate(), tandemmasterId, new SimpleAssignment(true));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(1,assignedResult.getPayload().getTandemmaster().size());
        Assert.assertEquals(tandemmasterId, assignedResult.getPayload().getTandemmaster().get(0).getFlyer().getId());
    }

    @Test
    public void testAssignTandemmasterToJumpday_TandemmasterNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                initialResult.getPayload().getDate(), "99999999", new SimpleAssignment(true));

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmasterToJumpday_JumpdayNotFound() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), tandemmaster.getId(), new SimpleAssignment(true));

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String tandemmasterId = assignTandemmaster(jumpday);

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(jumpday.getDate(), tandemmasterId, new SimpleAssignment(false));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(0, assignedResult.getPayload().getTandemmaster().size());
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove_NotAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(jumpday.getDate(), tandemmaster.getId(), new SimpleAssignment(false));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(0, assignedResult.getPayload().getTandemmaster().size());
    }

    private String assignTandemmaster(Jumpday jumpday) {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(jumpday);
        assertTrue(initialResult.isSuccess());
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                initialResult.getPayload().getDate(), tandemmaster.getId(), new SimpleAssignment(true));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(initialResult.getPayload().getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(1, assignedResult.getPayload().getTandemmaster().size());
        Assert.assertEquals(tandemmaster.getId(), assignedResult.getPayload().getTandemmaster().get(0).getFlyer().getId());

        return tandemmaster.getId();
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove_TandemmasterNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                initialResult.getPayload().getDate(), "99999999", new SimpleAssignment(true));

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove_JumpdayNotFound() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), tandemmaster.getId(), new SimpleAssignment(true));

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmaster_Addition() {
        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        saveAssignment(tandemmasterDetails);

        GenericResult<Jumpday> assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        GenericResult<Jumpday> assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        Assert.assertEquals(1, assignedResult1.getPayload().getTandemmaster().size());
        Assert.assertEquals(1, assignedResult2.getPayload().getTandemmaster().size());
    }

    private void saveAssignment(TandemmasterDetailsDTO tandemmasterDetails) {
        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testAssignTandemmaster_FromAlldayToTime() {
        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();

        saveAndCheckDayBasedAssignment(tandemmasterDetails);

        saveAndCheckTimeBasedAssignment(tandemmasterDetails);
    }

    @Test
    public void testAssignTandemmaster_FromTimeToAllday() {
        TandemmasterDetailsDTO tandemmaster = prepareJumpdaysAndTandemmaster();

        saveAndCheckTimeBasedAssignment(tandemmaster);

        saveAndCheckDayBasedAssignment(tandemmaster);
    }

    private void saveAndCheckTimeBasedAssignment(TandemmasterDetailsDTO tandemmasterDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true, false, LocalTime.of(13, 0), LocalTime.of(20, 0));
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(tandemmasterDetails);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(LocalDate.now());

        Assert.assertEquals(1, assignedResult.getPayload().getTandemmaster().size());
        Assert.assertFalse(assignedResult.getPayload().getTandemmaster().get(0).isAllday());
        Assert.assertEquals(LocalTime.of(13, 0), assignedResult.getPayload().getTandemmaster().get(0).getFrom());
        Assert.assertEquals(LocalTime.of(20, 0), assignedResult.getPayload().getTandemmaster().get(0).getTo());
    }

    private void saveAndCheckDayBasedAssignment(TandemmasterDetailsDTO tandemmasterDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(tandemmasterDetails);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(LocalDate.now());
        Assert.assertEquals(1, assignedResult.getPayload().getTandemmaster().size());
        Assert.assertTrue(assignedResult.getPayload().getTandemmaster().get(0).isAllday());
    }

    private TandemmasterDetailsDTO prepareJumpdaysAndTandemmaster() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        TandemmasterConverter converter = new TandemmasterConverter();
        return converter.convertToDetailsDto(tandemmaster, Map.of());
    }

    @Test
    public void testAssignTandemmaster_Removal() {
        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        tandemmasterService.assignTandemmasterToJumpday(LocalDate.now(), tandemmasterDetails.getId(), new SimpleAssignment(true));
        tandemmasterService.assignTandemmasterToJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS), tandemmasterDetails.getId(), new SimpleAssignment(true));

        GenericResult<Jumpday> assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        GenericResult<Jumpday> assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        Assert.assertEquals(1, assignedResult1.getPayload().getTandemmaster().size());
        Assert.assertEquals(1, assignedResult2.getPayload().getTandemmaster().size());


        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));
        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails);

        assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(0, assignedResult1.getPayload().getTandemmaster().size());
        Assert.assertEquals(0, assignedResult2.getPayload().getTandemmaster().size());
    }

    @Test
    public void testAssignTandemmaster_Error() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        TandemmasterConverter converter = new TandemmasterConverter();
        TandemmasterDetailsDTO tandemmasterDetails = converter.convertToDetailsDto(tandemmaster, Map.of());
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails);

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }
}
