package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
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
class VideoflyerServiceTest extends AbstractSkdvinTest {

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

        VideoflyerDetails videoflyerDetails = videoflyerService.getById(videoflyer.getId());

        assertNotNull(videoflyerDetails);
        assertEquals(2, videoflyerDetails.getAssignments().size());
        assertTrue(videoflyerDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(videoflyerDetails.getAssignments().get(nowPlus1).isAssigned());
    }

    @Test
    void testGetVideoflyerById_DoesNotContainPastDates() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(yesterday);
        jumpday1.getVideoflyer().add(createAssignment(videoflyer));
        jumpday2.getVideoflyer().add(createAssignment(videoflyer, false));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        VideoflyerDetails videoflyerDetails = videoflyerService.getById(videoflyer.getId());

        assertNotNull(videoflyerDetails);
        assertEquals(1, videoflyerDetails.getAssignments().size());
        assertTrue(videoflyerDetails.getAssignments().get(LocalDate.now()).isAssigned());
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

        VideoflyerDetails videoflyerDetails = videoflyerService.getByEmail(videoflyer.getEmail());

        assertNotNull(videoflyerDetails);
        assertEquals(2, videoflyerDetails.getAssignments().size());
        assertTrue(videoflyerDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(videoflyerDetails.getAssignments().get(nowPlus1).isAssigned());
    }

    @Test
    void testGetVideoflyerByEmail_DoesNotContainPastDates() {
        Videoflyer videoflyer1 = ModelMockHelper.createVideoflyer();
        videoflyer1.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);

        Videoflyer videoflyer = videoflyerRepository.save(videoflyer1);
        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(yesterday);
        jumpday1.getVideoflyer().add(createAssignment(videoflyer));
        jumpday2.getVideoflyer().add(createAssignment(videoflyer));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        VideoflyerDetails videoflyerDetails = videoflyerService.getByEmail(videoflyer.getEmail());

        assertNotNull(videoflyerDetails);
        assertEquals(1, videoflyerDetails.getAssignments().size());
        assertTrue(videoflyerDetails.getAssignments().get(LocalDate.now()).isAssigned());
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
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            videoflyerService.getById("99999999999")
        );

        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testGetVideoflyerByEmail_NotFound() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            videoflyerService.getByEmail("foo@example.com")
        );

        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testAssignVideoflyerToJumpday() {
        assignVideoflyer(ModelMockHelper.createJumpday());
    }

    @Test
    void testAssignVideoflyerToJumpday_EditVideoflyer_CheckAssignment() {
        assignVideoflyer(ModelMockHelper.createJumpday());

        Jumpday jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertFalse(jumpday.getVideoflyer().get(0).getFlyer().isPicAndVid());
        Optional<Videoflyer> videoflyerOptional = videoflyerRepository.findById(jumpday.getVideoflyer().get(0).getFlyer().getId());
        assertTrue(videoflyerOptional.isPresent());
        Videoflyer videoflyer = videoflyerOptional.get();
        assertFalse(videoflyer.isPicAndVid());

        videoflyer.setPicAndVid(true);
        Videoflyer updatedVideoflyer = videoflyerRepository.save(videoflyer);
        assertTrue(updatedVideoflyer.isPicAndVid());
        jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertTrue(jumpday.getVideoflyer().get(0).getFlyer().isPicAndVid());
    }

    @Test
    void testAssignVideoflyerToJumpday_AlreadyAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String videoflyerId = assignVideoflyer(jumpday);

        videoflyerService.assignVideoflyerToJumpday(
                jumpday.getDate(), videoflyerId, new SimpleAssignment(true));
        Jumpday assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertEquals(1,assignedResult.getVideoflyer().size());
        assertEquals(videoflyerId, assignedResult.getVideoflyer().get(0).getFlyer().getId());
    }

    @Test
    void testAssignVideoflyerToJumpday_VideoflyerNotFound() {
        Jumpday initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        LocalDate date = initialResult.getDate();
        SimpleAssignment assignment = new SimpleAssignment(true);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
            videoflyerService.assignVideoflyerToJumpday(date, "99999999", assignment)
        );

        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND, ex.getErrorMessage());
    }

    @Test
    void testAssignVideoflyerToJumpday_JumpdayNotFound() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        LocalDate date = LocalDate.now().plus(1, ChronoUnit.YEARS);
        String id = videoflyer.getId();
        SimpleAssignment assignment = new SimpleAssignment(true);


        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            videoflyerService.assignVideoflyerToJumpday(date, id, assignment)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String videoflyerId = assignVideoflyer(jumpday);

        videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyerId, new SimpleAssignment(false));
        Jumpday assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertEquals(0, assignedResult.getVideoflyer().size());
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove_NotAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyer.getId(), new SimpleAssignment(false));
        Jumpday assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertEquals(0, assignedResult.getVideoflyer().size());
    }

    private String assignVideoflyer(Jumpday jumpday) {
        Jumpday initialResult = jumpdayService.saveJumpday(jumpday);
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        videoflyerService.assignVideoflyerToJumpday(
                initialResult.getDate(), videoflyer.getId(), new SimpleAssignment(true));
        Jumpday assignedResult = jumpdayService.findJumpday(initialResult.getDate());

        assertEquals(1, assignedResult.getVideoflyer().size());
        assertEquals(videoflyer.getId(), assignedResult.getVideoflyer().get(0).getFlyer().getId());

        return videoflyer.getId();
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove_VideoflyerNotFound() {
        Jumpday initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        LocalDate date = initialResult.getDate();
        SimpleAssignment assignment = new SimpleAssignment(true);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
            videoflyerService.assignVideoflyerToJumpday(date, "99999999", assignment)
        );

        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND, ex.getErrorMessage());
    }

    @Test
    void testAssignVideoflyerToJumpday_Remove_JumpdayNotFound() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        LocalDate date = LocalDate.now().plus(1, ChronoUnit.YEARS);
        String id = videoflyer.getId();
        SimpleAssignment assignment = new SimpleAssignment(true);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            videoflyerService.assignVideoflyerToJumpday(date, id, assignment));

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

    @Test
    void testAssignVideoflyer_Addition() {
        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        saveAssignment(videoflyerDetails);

        Jumpday assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        Jumpday assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(1, assignedResult1.getVideoflyer().size());
        assertEquals(1, assignedResult2.getVideoflyer().size());
    }

    private void saveAssignment(VideoflyerDetails videoflyerDetails) {
        videoflyerService.assignVideoflyer(videoflyerDetails, false);
    }

    @Test
    void testAssignVideoflyer_FromAlldayToTime() {
        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();

        saveAndCheckDayBasedAssignment(videoflyerDetails);

        saveAndCheckTimeBasedAssignment(videoflyerDetails);
    }

    @Test
    void testAssignVideoflyer_FromTimeToAllday() {
        VideoflyerDetails videoflyer = prepareJumpdaysAndVideoflyer();

        saveAndCheckTimeBasedAssignment(videoflyer);

        saveAndCheckDayBasedAssignment(videoflyer);
    }

    private void saveAndCheckTimeBasedAssignment(VideoflyerDetails videoflyerDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true, false, LocalTime.of(13, 0), LocalTime.of(20, 0));
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(videoflyerDetails);
        Jumpday assignedResult = jumpdayService.findJumpday(LocalDate.now());

        assertEquals(1, assignedResult.getVideoflyer().size());
        assertFalse(assignedResult.getVideoflyer().get(0).isAllday());
        assertEquals(LocalTime.of(13, 0), assignedResult.getVideoflyer().get(0).getFrom());
        assertEquals(LocalTime.of(20, 0), assignedResult.getVideoflyer().get(0).getTo());
    }

    private void saveAndCheckDayBasedAssignment(VideoflyerDetails videoflyerDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(videoflyerDetails);
        Jumpday assignedResult = jumpdayService.findJumpday(LocalDate.now());

        assertEquals(1, assignedResult.getVideoflyer().size());
        assertTrue(assignedResult.getVideoflyer().get(0).isAllday());
    }

    private VideoflyerDetails prepareJumpdaysAndVideoflyer() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        VideoflyerConverter converter = new VideoflyerConverter();
        return converter.convertToDetails(videoflyer, Map.of());
    }

    @Test
    void testAssignVideoflyer_Removal() {
        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS), videoflyerDetails.getId(), new SimpleAssignment(true));

        Jumpday assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        Jumpday assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(1, assignedResult1.getVideoflyer().size());
        assertEquals(1, assignedResult2.getVideoflyer().size());

        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));
        videoflyerService.assignVideoflyer(videoflyerDetails, false);

        assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(0, assignedResult1.getVideoflyer().size());
        assertEquals(0, assignedResult2.getVideoflyer().size());
    }

    @Test
    void testAssignVideoflyer_Error() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        VideoflyerConverter converter = new VideoflyerConverter();
        VideoflyerDetails videoflyerDetails = converter.convertToDetails(videoflyer, Map.of());
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            videoflyerService.assignVideoflyer(videoflyerDetails, false)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }


    @Test
    void testDeleteVideoflyer() {
        String id = videoflyerRepository.save(ModelMockHelper.createVideoflyer()).getId();

        videoflyerService.delete(id);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            videoflyerService.getById(id)
        );

        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testDeleteVideoflyer_DeletesAssignments() {
        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(videoflyerDetails);

        Jumpday assignedResult = jumpdayService.findJumpday(LocalDate.now());
        assertEquals(1, assignedResult.getVideoflyer().size());

        String id = videoflyerDetails.getId();
        videoflyerService.delete(id);
        assignedResult = jumpdayService.findJumpday(LocalDate.now());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            videoflyerService.getById(id)
        );

        assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND, notFoundException.getErrorMessage());
        assertEquals(0, assignedResult.getVideoflyer().size());
    }

    @Test
    void testAssignVideoflyer_READONLY() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.READONLY);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
            videoflyerService.assignVideoflyer(videoflyerDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_READONLY, ex.getErrorMessage());
    }

    @Test
    void testAssignVideoflyer_Removal_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));

        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
            videoflyerService.assignVideoflyer(videoflyerDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE, ex.getErrorMessage());
    }

    @Test
    void testAssignVideoflyer_RemovalNotAllday_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));

        videoflyerDetails.setAssignments(Map.of(LocalDate.now(),
                new SimpleAssignment(true, false, LocalTime.of(12, 0), LocalTime.of(20, 0))));

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
            videoflyerService.assignVideoflyer(videoflyerDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE, ex.getErrorMessage());
    }

    @Test
    void testAssignVideoflyer_RemovalNotAssigned_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        VideoflyerDetails videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), new SimpleAssignment(true));

        videoflyerDetails.setAssignments(new HashMap<>());

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
            videoflyerService.assignVideoflyer(videoflyerDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE, ex.getErrorMessage());
    }
}
