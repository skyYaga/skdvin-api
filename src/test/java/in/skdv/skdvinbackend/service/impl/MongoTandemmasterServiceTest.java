package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MongoTandemmasterServiceTest extends AbstractSkdvinTest {

    @MockBean
    ISettingsService settingsService;

    @Autowired
    JumpdayRepository jumpdayRepository;

    @Autowired
    TandemmasterRepository tandemmasterRepository;

    @Autowired
    ITandemmasterService tandemmasterService;

    @Autowired
    IJumpdayService jumpdayService;

    @BeforeEach
    public void setup() {
        jumpdayRepository.deleteAll();
        tandemmasterRepository.deleteAll();
    }

    @Test
    public void testGetTandemmasterById() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        LocalDate nowPlus1 = LocalDate.now().plus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(nowPlus1);
        jumpday1.getTandemmaster().add(createAssignment(tandemmaster));
        jumpday2.getTandemmaster().add(createAssignment(tandemmaster, false));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        TandemmasterDetailsDTO tandemmasterDetails = tandemmasterService.getById(tandemmaster.getId());

        assertNotNull(tandemmasterDetails);
        assertEquals(2, tandemmasterDetails.getAssignments().size());
        assertTrue(tandemmasterDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(tandemmasterDetails.getAssignments().get(nowPlus1).isAssigned());
    }

    @Test
    public void testGetTandemmasterByEmail() {
        Tandemmaster tandemmaster1 = ModelMockHelper.createTandemmaster();
        tandemmaster1.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);

        Tandemmaster tandemmaster = tandemmasterRepository.save(tandemmaster1);
        LocalDate nowPlus1 = LocalDate.now().plus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(nowPlus1);
        jumpday1.getTandemmaster().add(createAssignment(tandemmaster));
        jumpday2.getTandemmaster().add(createAssignment(tandemmaster, false));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        TandemmasterDetailsDTO tandemmasterDetails = tandemmasterService.getByEmail(tandemmaster.getEmail());

        assertNotNull(tandemmasterDetails);
        assertEquals(2, tandemmasterDetails.getAssignments().size());
        assertTrue(tandemmasterDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(tandemmasterDetails.getAssignments().get(nowPlus1).isAssigned());
    }

    private Assignment<Tandemmaster> createAssignment(Tandemmaster tandemmaster, boolean assigned) {
        Assignment<Tandemmaster> assignment = new Assignment<>();
        assignment.setFlyer(tandemmaster);
        assignment.setAssigned(assigned);
        return assignment;
    }

    private Assignment<Tandemmaster> createAssignment(Tandemmaster tandemmaster) {
        return createAssignment(tandemmaster, true);
    }

    @Test
    public void testGetTandemmasterById_NotFound() {
        TandemmasterDetailsDTO tandemmasterDetails = tandemmasterService.getById("99999999999");
        assertNull(tandemmasterDetails);
    }

    @Test
    public void testGetTandemmasterByEmail_NotFound() {
        TandemmasterDetailsDTO tandemmasterDetails = tandemmasterService.getByEmail("foo@example.com");
        assertNull(tandemmasterDetails);
    }

    @Test
    public void testAssignTandemmasterToJumpday() {
        assignTandemmaster(ModelMockHelper.createJumpday());
    }

    @Test
    public void testAssignTandemmasterToJumpday_EditTandemmaster_CheckAssignment() {
        assignTandemmaster(ModelMockHelper.createJumpday());

        GenericResult<Jumpday> jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertFalse(jumpday.getPayload().getTandemmaster().get(0).getFlyer().isHandcam());
        Optional<Tandemmaster> tandemmasterOptional = tandemmasterRepository.findById(jumpday.getPayload().getTandemmaster().get(0).getFlyer().getId());
        assertTrue(tandemmasterOptional.isPresent());
        Tandemmaster tandemmaster = tandemmasterOptional.get();
        assertFalse(tandemmaster.isHandcam());

        tandemmaster.setHandcam(true);
        Tandemmaster updatedTandemmaster = tandemmasterRepository.save(tandemmaster);
        assertTrue(updatedTandemmaster.isHandcam());
        jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertTrue(jumpday.getPayload().getTandemmaster().get(0).getFlyer().isHandcam());
    }

    @Test
    public void testAssignTandemmasterToJumpday_AlreadyAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String tandemmasterId = assignTandemmaster(jumpday);

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                jumpday.getDate(), tandemmasterId, new SimpleAssignment(true));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(1,assignedResult.getPayload().getTandemmaster().size());
        assertEquals(tandemmasterId, assignedResult.getPayload().getTandemmaster().get(0).getFlyer().getId());
    }

    @Test
    public void testAssignTandemmasterToJumpday_TandemmasterNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                initialResult.getPayload().getDate(), "99999999", new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmasterToJumpday_JumpdayNotFound() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), tandemmaster.getId(), new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String tandemmasterId = assignTandemmaster(jumpday);

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(jumpday.getDate(), tandemmasterId, new SimpleAssignment(false));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(0, assignedResult.getPayload().getTandemmaster().size());
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove_NotAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(jumpday.getDate(), tandemmaster.getId(), new SimpleAssignment(false));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(0, assignedResult.getPayload().getTandemmaster().size());
    }

    private String assignTandemmaster(Jumpday jumpday) {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(jumpday);
        assertTrue(initialResult.isSuccess());
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                initialResult.getPayload().getDate(), tandemmaster.getId(), new SimpleAssignment(true));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(initialResult.getPayload().getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(1, assignedResult.getPayload().getTandemmaster().size());
        assertEquals(tandemmaster.getId(), assignedResult.getPayload().getTandemmaster().get(0).getFlyer().getId());

        return tandemmaster.getId();
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove_TandemmasterNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                initialResult.getPayload().getDate(), "99999999", new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmasterToJumpday_Remove_JumpdayNotFound() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        GenericResult<Void> result = tandemmasterService.assignTandemmasterToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), tandemmaster.getId(), new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmaster_Addition() {
        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        saveAssignment(tandemmasterDetails);

        GenericResult<Jumpday> assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        GenericResult<Jumpday> assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(1, assignedResult1.getPayload().getTandemmaster().size());
        assertEquals(1, assignedResult2.getPayload().getTandemmaster().size());
    }

    private void saveAssignment(TandemmasterDetailsDTO tandemmasterDetails) {
        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails, false);
        assertTrue(result.isSuccess());
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

        assertEquals(1, assignedResult.getPayload().getTandemmaster().size());
        assertFalse(assignedResult.getPayload().getTandemmaster().get(0).isAllday());
        assertEquals(LocalTime.of(13, 0), assignedResult.getPayload().getTandemmaster().get(0).getFrom());
        assertEquals(LocalTime.of(20, 0), assignedResult.getPayload().getTandemmaster().get(0).getTo());
    }

    private void saveAndCheckDayBasedAssignment(TandemmasterDetailsDTO tandemmasterDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(tandemmasterDetails);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(LocalDate.now());
        assertEquals(1, assignedResult.getPayload().getTandemmaster().size());
        assertTrue(assignedResult.getPayload().getTandemmaster().get(0).isAllday());
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

        assertEquals(1, assignedResult1.getPayload().getTandemmaster().size());
        assertEquals(1, assignedResult2.getPayload().getTandemmaster().size());


        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));
        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertTrue(result.isSuccess());
        assertEquals(0, assignedResult1.getPayload().getTandemmaster().size());
        assertEquals(0, assignedResult2.getPayload().getTandemmaster().size());
    }

    @Test
    public void testAssignTandemmaster_Error() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        TandemmasterConverter converter = new TandemmasterConverter();
        TandemmasterDetailsDTO tandemmasterDetails = converter.convertToDetailsDto(tandemmaster, Map.of());
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testDeleteTandemmaster() {
        String id = tandemmasterRepository.save(ModelMockHelper.createTandemmaster()).getId();

        tandemmasterService.delete(id);

        assertNull(tandemmasterService.getById(id));
    }

    @Test
    public void testDeleteTandemmaster_DeletesAssignments() {
        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(tandemmasterDetails);

        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(LocalDate.now());
        assertEquals(1, assignedResult.getPayload().getTandemmaster().size());

        tandemmasterService.delete(tandemmasterDetails.getId());
        assignedResult = jumpdayService.findJumpday(LocalDate.now());

        assertNull(tandemmasterService.getById(tandemmasterDetails.getId()));
        assertEquals(0, assignedResult.getPayload().getTandemmaster().size());
    }


    @Test
    public void testAssignTandemmaster_READONLY() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.READONLY);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_READONLY.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmaster_Removal_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        tandemmasterService.assignTandemmasterToJumpday(LocalDate.now(), tandemmasterDetails.getId(), new SimpleAssignment(true));

        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmaster_RemovalNotAllday_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        tandemmasterService.assignTandemmasterToJumpday(LocalDate.now(), tandemmasterDetails.getId(), new SimpleAssignment(true));

        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(),
                new SimpleAssignment(true, false, LocalTime.of(12, 0), LocalTime.of(20, 0))));

        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE.toString(), result.getMessage());
    }

    @Test
    public void testAssignTandemmaster_RemovalNotAssigned_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetailsDTO tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        tandemmasterService.assignTandemmasterToJumpday(LocalDate.now(), tandemmasterDetails.getId(), new SimpleAssignment(true));

        tandemmasterDetails.setAssignments(new HashMap<>());

        GenericResult<Void> result = tandemmasterService.assignTandemmaster(tandemmasterDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE.toString(), result.getMessage());
    }

}
