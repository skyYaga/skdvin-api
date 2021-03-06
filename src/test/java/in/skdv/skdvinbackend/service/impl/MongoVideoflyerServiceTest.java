package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
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
class MongoVideoflyerServiceTest extends AbstractSkdvinTest {

    @MockBean
    ISettingsService settingsService;

    @Autowired
    JumpdayRepository jumpdayRepository;

    @Autowired
    VideoflyerRepository videoflyerRepository;

    @Autowired
    IVideoflyerService videoflyerService;

    @Autowired
    IJumpdayService jumpdayService;

    @BeforeEach
    void setup() {
        jumpdayRepository.deleteAll();
        videoflyerRepository.deleteAll();
    }

    @Test
    void testGetVideoflyerById() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        LocalDate nowPlus1 = LocalDate.now().plus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(nowPlus1);
        jumpday1.getVideoflyer().add(createAssignment(videoflyer));
        jumpday2.getVideoflyer().add(createAssignment(videoflyer, false));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        VideoflyerDetailsDTO videoflyerDetails = videoflyerService.getById(videoflyer.getId());

        assertNotNull(videoflyerDetails);
        assertEquals(2, videoflyerDetails.getAssignments().size());
        assertTrue(videoflyerDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(videoflyerDetails.getAssignments().get(nowPlus1).isAssigned());
    }


    @Test
    void testGetVideoflyerByEmail() {
        Videoflyer videoflyer1 = ModelMockHelper.createVideoflyer();
        videoflyer1.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);

        Videoflyer videoflyer = videoflyerRepository.save(videoflyer1);
        LocalDate nowPlus1 = LocalDate.now().plus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(nowPlus1);
        jumpday1.getVideoflyer().add(createAssignment(videoflyer));
        jumpday2.getVideoflyer().add(createAssignment(videoflyer, false));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        VideoflyerDetailsDTO videoflyerDetails = videoflyerService.getByEmail(videoflyer.getEmail());

        assertNotNull(videoflyerDetails);
        assertEquals(2, videoflyerDetails.getAssignments().size());
        assertTrue(videoflyerDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(videoflyerDetails.getAssignments().get(nowPlus1).isAssigned());
    }


    private Assignment<Videoflyer> createAssignment(Videoflyer videoflyer, boolean assigned) {
        Assignment<Videoflyer> assignment = new Assignment<>();
        assignment.setFlyer(videoflyer);
        assignment.setAssigned(assigned);
        return assignment;
    }

    private Assignment<Videoflyer> createAssignment(Videoflyer videoflyer) {
        return createAssignment(videoflyer, true);
    }

    @Test
    void testGetVideoflyerById_NotFound() {
        VideoflyerDetailsDTO videoflyerDetails = videoflyerService.getById("99999999999");
        assertNull(videoflyerDetails);
    }

    @Test
    void testGetVideoflyerByEmail_NotFound() {
        VideoflyerDetailsDTO videoflyerDetails = videoflyerService.getByEmail("foo@example.com");
        assertNull(videoflyerDetails);
    }

    @Test
    void testAssignVideoflyerToJumpday() {
        assignVideoflyer(ModelMockHelper.createJumpday());
    }

    @Test
    void testAssignVideoflyerToJumpday_EditVideoflyer_CheckAssignment() {
        assignVideoflyer(ModelMockHelper.createJumpday());

        GenericResult<Jumpday> jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertFalse(jumpday.getPayload().getVideoflyer().get(0).getFlyer().isPicAndVid());
        Optional<Videoflyer> videoflyerOptional = videoflyerRepository.findById(jumpday.getPayload().getVideoflyer().get(0).getFlyer().getId());
        assertTrue(videoflyerOptional.isPresent());
        Videoflyer videoflyer = videoflyerOptional.get();
        assertFalse(videoflyer.isPicAndVid());

        videoflyer.setPicAndVid(true);
        Videoflyer updatedVideoflyer = videoflyerRepository.save(videoflyer);
        assertTrue(updatedVideoflyer.isPicAndVid());
        jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertTrue(jumpday.getPayload().getVideoflyer().get(0).getFlyer().isPicAndVid());
    }

    @Test
    void testAssignVideoflyerToJumpday_AlreadyAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String videoflyerId = assignVideoflyer(jumpday);

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                jumpday.getDate(), videoflyerId, new SimpleAssignment(true));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(1,assignedResult.getPayload().getVideoflyer().size());
        assertEquals(videoflyerId, assignedResult.getPayload().getVideoflyer().get(0).getFlyer().getId());
    }

    @Test
    void testAssignVideoflyerToJumpday_VideoflyerNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                initialResult.getPayload().getDate(), "99999999", new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    void testAssignVideoflyerToJumpday_JumpdayNotFound() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), videoflyer.getId(), new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String videoflyerId = assignVideoflyer(jumpday);

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyerId, new SimpleAssignment(false));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(0, assignedResult.getPayload().getVideoflyer().size());
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove_NotAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyer.getId(), new SimpleAssignment(false));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(0, assignedResult.getPayload().getVideoflyer().size());
    }

    private String assignVideoflyer(Jumpday jumpday) {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(jumpday);
        assertTrue(initialResult.isSuccess());
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                initialResult.getPayload().getDate(), videoflyer.getId(), new SimpleAssignment(true));
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(initialResult.getPayload().getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(assignedResult.isSuccess());
        assertEquals(1, assignedResult.getPayload().getVideoflyer().size());
        assertEquals(videoflyer.getId(), assignedResult.getPayload().getVideoflyer().get(0).getFlyer().getId());

        return videoflyer.getId();
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove_VideoflyerNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                initialResult.getPayload().getDate(), "99999999", new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove_JumpdayNotFound() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), videoflyer.getId(), new SimpleAssignment(true));

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    void testAssignVideoflyer_Addition() {
        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        saveAssignment(videoflyerDetails);

        GenericResult<Jumpday> assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        GenericResult<Jumpday> assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(1, assignedResult1.getPayload().getVideoflyer().size());
        assertEquals(1, assignedResult2.getPayload().getVideoflyer().size());
    }

    private void saveAssignment(VideoflyerDetailsDTO videoflyerDetails) {
        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails, false);
        assertTrue(result.isSuccess());
    }

    @Test
    void testAssignVideoflyer_FromAlldayToTime() {
        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();

        saveAndCheckDayBasedAssignment(videoflyerDetails);

        saveAndCheckTimeBasedAssignment(videoflyerDetails);
    }

    @Test
    void testAssignVideoflyer_FromTimeToAllday() {
        VideoflyerDetailsDTO videoflyer = prepareJumpdaysAndVideoflyer();

        saveAndCheckTimeBasedAssignment(videoflyer);

        saveAndCheckDayBasedAssignment(videoflyer);
    }

    private void saveAndCheckTimeBasedAssignment(VideoflyerDetailsDTO videoflyerDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true, false, LocalTime.of(13, 0), LocalTime.of(20, 0));
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(videoflyerDetails);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(LocalDate.now());

        assertEquals(1, assignedResult.getPayload().getVideoflyer().size());
        assertFalse(assignedResult.getPayload().getVideoflyer().get(0).isAllday());
        assertEquals(LocalTime.of(13, 0), assignedResult.getPayload().getVideoflyer().get(0).getFrom());
        assertEquals(LocalTime.of(20, 0), assignedResult.getPayload().getVideoflyer().get(0).getTo());
    }

    private void saveAndCheckDayBasedAssignment(VideoflyerDetailsDTO videoflyerDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(videoflyerDetails);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(LocalDate.now());
        assertEquals(1, assignedResult.getPayload().getVideoflyer().size());
        assertTrue(assignedResult.getPayload().getVideoflyer().get(0).isAllday());
    }

    private VideoflyerDetailsDTO prepareJumpdaysAndVideoflyer() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        VideoflyerConverter converter = new VideoflyerConverter();
        return converter.convertToDetailsDto(videoflyer, Map.of());
    }

    @Test
    void testAssignVideoflyer_Removal() {
        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS), videoflyerDetails.getId(), new SimpleAssignment(true));

        GenericResult<Jumpday> assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        GenericResult<Jumpday> assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(1, assignedResult1.getPayload().getVideoflyer().size());
        assertEquals(1, assignedResult2.getPayload().getVideoflyer().size());


        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));
        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails, false);

        assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertTrue(result.isSuccess());
        assertEquals(0, assignedResult1.getPayload().getVideoflyer().size());
        assertEquals(0, assignedResult2.getPayload().getVideoflyer().size());
    }

    @Test
    void testAssignVideoflyer_Error() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        VideoflyerConverter converter = new VideoflyerConverter();
        VideoflyerDetailsDTO videoflyerDetails = converter.convertToDetailsDto(videoflyer, Map.of());
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails, false);

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }


    @Test
    void testDeleteVideoflyer() {
        String id = videoflyerRepository.save(ModelMockHelper.createVideoflyer()).getId();

        videoflyerService.delete(id);

        assertNull(videoflyerService.getById(id));
    }

    @Test
    void testDeleteVideoflyer_DeletesAssignments() {
        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(videoflyerDetails);

        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(LocalDate.now());
        assertEquals(1, assignedResult.getPayload().getVideoflyer().size());

        videoflyerService.delete(videoflyerDetails.getId());
        assignedResult = jumpdayService.findJumpday(LocalDate.now());

        assertNull(videoflyerService.getById(videoflyerDetails.getId()));
        assertEquals(0, assignedResult.getPayload().getVideoflyer().size());
    }

    @Test
    void testAssignVideoflyer_READONLY() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.READONLY);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_READONLY.toString(), result.getMessage());
    }

    @Test
    void testAssignVideoflyer_Removal_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));

        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE.toString(), result.getMessage());
    }

    @Test
    void testAssignVideoflyer_RemovalNotAllday_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));

        videoflyerDetails.setAssignments(Map.of(LocalDate.now(),
                new SimpleAssignment(true, false, LocalTime.of(12, 0), LocalTime.of(20, 0))));

        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE.toString(), result.getMessage());
    }

    @Test
    void testAssignVideoflyer_RemovalNotAssigned_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));

        videoflyerDetails.setAssignments(new HashMap<>());

        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails, true);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE.toString(), result.getMessage());
    }
}
