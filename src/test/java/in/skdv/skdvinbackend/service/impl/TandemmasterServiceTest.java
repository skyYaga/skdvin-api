package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.TandemmasterDetails;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class TandemmasterServiceTest extends AbstractSkdvinTest {

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

    TandemmasterConverter converter = new TandemmasterConverter();

    @BeforeEach
    void setup() {
        jumpdayRepository.deleteAll();
        tandemmasterRepository.deleteAll();
    }

    @Test
    void testFindAll_isSorted() {
        Tandemmaster tandemmaster2 = ModelMockHelper.createTandemmaster("Fav", "Flyer");
        tandemmaster2.setFavorite(true);
        Tandemmaster tandemmaster3 = ModelMockHelper.createTandemmaster("SecondFav", "Flyer");
        tandemmaster3.setFavorite(true);

        tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmasterRepository.save(tandemmaster2);
        tandemmasterRepository.save(tandemmaster3);

        List<Tandemmaster> tandemmasters = tandemmasterService.findAll();

        assertNotNull(tandemmasters);
        assertEquals(3, tandemmasters.size());
        assertTrue(tandemmasters.get(0).isFavorite());
        assertEquals("Fav", tandemmasters.get(0).getFirstName());
        assertTrue(tandemmasters.get(1).isFavorite());
        assertEquals("SecondFav", tandemmasters.get(1).getFirstName());
        assertFalse(tandemmasters.get(2).isFavorite());
        assertEquals("Max", tandemmasters.get(2).getFirstName());
    }

    @Test
    void testGetTandemmasterById() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        LocalDate nowPlus1 = LocalDate.now().plus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(nowPlus1);
        jumpday1.getTandemmaster().add(createAssignment(tandemmaster));
        jumpday2.getTandemmaster().add(createAssignment(tandemmaster, false));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        TandemmasterDetails tandemmasterDetails = tandemmasterService.getById(tandemmaster.getId());

        assertNotNull(tandemmasterDetails);
        assertEquals(2, tandemmasterDetails.getAssignments().size());
        assertTrue(tandemmasterDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(tandemmasterDetails.getAssignments().get(nowPlus1).isAssigned());
    }

    @Test
    void testGetTandemmasterById_DoesNotContainPastDates() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(yesterday);
        jumpday1.getTandemmaster().add(createAssignment(tandemmaster));
        jumpday2.getTandemmaster().add(createAssignment(tandemmaster));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        TandemmasterDetails tandemmasterDetails = tandemmasterService.getById(tandemmaster.getId());

        assertNotNull(tandemmasterDetails);
        assertEquals(1, tandemmasterDetails.getAssignments().size());
        assertTrue(tandemmasterDetails.getAssignments().get(LocalDate.now()).isAssigned());
    }

    @Test
    void testGetTandemmasterByEmail() {
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

        TandemmasterDetails tandemmasterDetails = tandemmasterService.getByEmail(tandemmaster.getEmail());

        assertNotNull(tandemmasterDetails);
        assertEquals(2, tandemmasterDetails.getAssignments().size());
        assertTrue(tandemmasterDetails.getAssignments().get(LocalDate.now()).isAssigned());
        assertFalse(tandemmasterDetails.getAssignments().get(nowPlus1).isAssigned());
    }

    @Test
    void testGetTandemmasterByEmail_DoesNotContainPastDates() {
        Tandemmaster tandemmaster1 = ModelMockHelper.createTandemmaster();
        tandemmaster1.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);

        Tandemmaster tandemmaster = tandemmasterRepository.save(tandemmaster1);
        LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);

        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(yesterday);
        jumpday1.getTandemmaster().add(createAssignment(tandemmaster));
        jumpday2.getTandemmaster().add(createAssignment(tandemmaster));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        TandemmasterDetails tandemmasterDetails = tandemmasterService.getByEmail(tandemmaster.getEmail());

        assertNotNull(tandemmasterDetails);
        assertEquals(1, tandemmasterDetails.getAssignments().size());
        assertTrue(tandemmasterDetails.getAssignments().get(LocalDate.now()).isAssigned());
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
    void testGetTandemmasterById_NotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                tandemmasterService.getById("99999999999"));

        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND, ex.getErrorMessage());
    }

    @Test
    void testGetTandemmasterByEmail_NotFound() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                tandemmasterService.getByEmail("foo@example.com")
        );

        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testAssignTandemmasterToJumpday() {
        assignTandemmaster(ModelMockHelper.createJumpday());
    }

    @Test
    void testAssignTandemmasterToJumpday_EditTandemmaster_CheckAssignment() {
        assignTandemmaster(ModelMockHelper.createJumpday());

        Jumpday jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertFalse(jumpday.getTandemmaster().get(0).getFlyer().isHandcam());
        Optional<Tandemmaster> tandemmasterOptional = tandemmasterRepository.findById(jumpday.getTandemmaster().get(0).getFlyer().getId());
        assertTrue(tandemmasterOptional.isPresent());
        Tandemmaster tandemmaster = tandemmasterOptional.get();
        assertFalse(tandemmaster.isHandcam());

        tandemmaster.setHandcam(true);
        Tandemmaster updatedTandemmaster = tandemmasterRepository.save(tandemmaster);
        assertTrue(updatedTandemmaster.isHandcam());
        jumpday = jumpdayService.findJumpday(LocalDate.now());
        assertTrue(jumpday.getTandemmaster().get(0).getFlyer().isHandcam());
    }

    @Test
    void testAssignTandemmasterToJumpday_AlreadyAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Tandemmaster tandemmaster = assignTandemmaster(jumpday);

        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, jumpday.getDate());
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);
        Jumpday assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertEquals(1, assignedResult.getTandemmaster().size());
        assertEquals(tandemmaster.getId(), assignedResult.getTandemmaster().get(0).getFlyer().getId());
    }

    @Test
    void testAssignTandemmasterToJumpday_TandemmasterNotFound() {
        Jumpday initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        LocalDate date = initialResult.getDate();
        Tandemmaster tandemmaster = new Tandemmaster();
        tandemmaster.setId("99999999");
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, date);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, false)
        );

        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND, ex.getErrorMessage());
    }

    @Test
    void testAssignTandemmasterToJumpday_JumpdayNotFound() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        LocalDate date = LocalDate.now().plus(1, ChronoUnit.YEARS);
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, date);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, false)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

    @Test
    void testAssignTandemmasterToJumpday_Remove() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Tandemmaster tandemmaster = assignTandemmaster(jumpday);
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.removeTandemmasterAssignment(tandemmaster, jumpday.getDate());

        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);
        Jumpday assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertEquals(0, assignedResult.getTandemmaster().size());
    }

    @Test
    void testAssignTandemmasterToJumpday_Remove_NotAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.removeTandemmasterAssignment(tandemmaster, jumpday.getDate());

        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);
        Jumpday assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        assertEquals(0, assignedResult.getTandemmaster().size());
    }

    private Tandemmaster assignTandemmaster(Jumpday jumpday) {
        Jumpday initialResult = jumpdayService.saveJumpday(jumpday);
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, initialResult.getDate());
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);
        Jumpday assignedResult = jumpdayService.findJumpday(initialResult.getDate());

        assertEquals(1, assignedResult.getTandemmaster().size());
        assertEquals(tandemmaster.getId(), assignedResult.getTandemmaster().get(0).getFlyer().getId());

        return tandemmaster;
    }

    @Test
    void testAssignTandemmasterToJumpday_Remove_TandemmasterNotFound() {
        Jumpday initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        LocalDate date = initialResult.getDate();
        Tandemmaster tandemmaster = new Tandemmaster();
        tandemmaster.setId("99999999");
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, date);

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, false)
        );

        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND, ex.getErrorMessage());
    }

    @Test
    void testAssignTandemmasterToJumpday_Remove_JumpdayNotFound() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        LocalDate date = LocalDate.now().plus(1, ChronoUnit.YEARS);
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, date);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, false)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

    @Test
    void testAssignTandemmaster_Addition() {
        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        saveAssignment(tandemmasterDetails);

        Jumpday assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        Jumpday assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(1, assignedResult1.getTandemmaster().size());
        assertEquals(1, assignedResult2.getTandemmaster().size());
    }

    private void saveAssignment(TandemmasterDetails tandemmasterDetails) {
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);
    }

    @Test
    void testAssignTandemmaster_FromAlldayToTime() {
        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();

        saveAndCheckDayBasedAssignment(tandemmasterDetails);

        saveAndCheckTimeBasedAssignment(tandemmasterDetails);
    }

    @Test
    void testAssignTandemmaster_FromTimeToAllday() {
        TandemmasterDetails tandemmaster = prepareJumpdaysAndTandemmaster();

        saveAndCheckTimeBasedAssignment(tandemmaster);

        saveAndCheckDayBasedAssignment(tandemmaster);
    }

    private void saveAndCheckTimeBasedAssignment(TandemmasterDetails tandemmasterDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true, false, "", LocalTime.of(13, 0), LocalTime.of(20, 0));
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(tandemmasterDetails);
        Jumpday assignedResult = jumpdayService.findJumpday(LocalDate.now());

        assertEquals(1, assignedResult.getTandemmaster().size());
        assertFalse(assignedResult.getTandemmaster().get(0).isAllday());
        assertEquals(LocalTime.of(13, 0), assignedResult.getTandemmaster().get(0).getFrom());
        assertEquals(LocalTime.of(20, 0), assignedResult.getTandemmaster().get(0).getTo());
    }

    private void saveAndCheckDayBasedAssignment(TandemmasterDetails tandemmasterDetails) {
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(tandemmasterDetails);
        Jumpday assignedResult = jumpdayService.findJumpday(LocalDate.now());
        assertEquals(1, assignedResult.getTandemmaster().size());
        assertTrue(assignedResult.getTandemmaster().get(0).isAllday());
    }

    private TandemmasterDetails prepareJumpdaysAndTandemmaster() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        return converter.convertToDetails(tandemmaster, new HashMap<>());
    }

    @Test
    void testAssignTandemmaster_Removal() {
        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        tandemmasterDetails.setAssignments(Map.of(
                LocalDate.now(), new SimpleAssignment(true),
                LocalDate.now().plusDays(1), new SimpleAssignment(true)));

        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        Jumpday assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        Jumpday assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(1, assignedResult1.getTandemmaster().size());
        assertEquals(1, assignedResult2.getTandemmaster().size());

        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertEquals(0, assignedResult1.getTandemmaster().size());
        assertEquals(0, assignedResult2.getTandemmaster().size());
    }

    @Test
    void testAssignTandemmaster_Error() {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        TandemmasterConverter converter = new TandemmasterConverter();
        TandemmasterDetails tandemmasterDetails = converter.convertToDetails(tandemmaster, Map.of());
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, false)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

    @Test
    void testDeleteTandemmaster() {
        String id = tandemmasterRepository.save(ModelMockHelper.createTandemmaster()).getId();

        tandemmasterService.delete(id);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                tandemmasterService.getById(id)
        );

        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testDeleteTandemmaster_DeletesAssignments() {
        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO));

        saveAssignment(tandemmasterDetails);

        Jumpday assignedResult = jumpdayService.findJumpday(LocalDate.now());
        assertEquals(1, assignedResult.getTandemmaster().size());

        String tandemmasterId = tandemmasterDetails.getId();
        tandemmasterService.delete(tandemmasterId);
        assignedResult = jumpdayService.findJumpday(LocalDate.now());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                tandemmasterService.getById(tandemmasterId)
        );

        assertEquals(ErrorMessage.TANDEMMASTER_NOT_FOUND, notFoundException.getErrorMessage());
        assertEquals(0, assignedResult.getTandemmaster().size());
    }


    @Test
    void testAssignTandemmaster_READONLY() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.READONLY);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        SimpleAssignment assignmentDTO = new SimpleAssignment(true);
        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), assignmentDTO, LocalDate.now().plus(1, ChronoUnit.DAYS), assignmentDTO));

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_READONLY, ex.getErrorMessage());
    }

    @Test
    void testAssignTandemmaster_Removal_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        ModelMockHelper.addTandemmasterAssignment(tandemmasterDetails, LocalDate.now());
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(), new SimpleAssignment(false), LocalDate.now().plus(1, ChronoUnit.DAYS), new SimpleAssignment(false)));

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE, ex.getErrorMessage());
    }

    @Test
    void testAssignTandemmaster_RemovalNotAllday_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        ModelMockHelper.addTandemmasterAssignment(tandemmasterDetails, LocalDate.now());
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        tandemmasterDetails.setAssignments(Map.of(LocalDate.now(),
                new SimpleAssignment(true, false, "Example note", LocalTime.of(12, 0), LocalTime.of(20, 0))));

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE, ex.getErrorMessage());
    }

    @Test
    void testAssignTandemmaster_RemovalNotAssigned_NODELETE() {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.NODELETE);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        TandemmasterDetails tandemmasterDetails = prepareJumpdaysAndTandemmaster();
        ModelMockHelper.addTandemmasterAssignment(tandemmasterDetails, LocalDate.now());
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        tandemmasterDetails.setAssignments(new HashMap<>());

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                tandemmasterService.assignTandemmaster(tandemmasterDetails, true)
        );

        assertEquals(ErrorMessage.SELFASSIGNMENT_NODELETE, ex.getErrorMessage());
    }

}
