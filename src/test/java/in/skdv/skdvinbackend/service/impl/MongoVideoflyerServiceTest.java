package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoVideoflyerServiceTest extends AbstractSkdvinTest {

    @Autowired
    JumpdayRepository jumpdayRepository;

    @Autowired
    VideoflyerRepository videoflyerRepository;

    @Autowired
    IVideoflyerService videoflyerService;

    @Autowired
    IJumpdayService jumpdayService;

    @Before
    public void setup() {
        jumpdayRepository.deleteAll();
        videoflyerRepository.deleteAll();
    }

    @Test
    public void testGetVideoflyerById() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        jumpday1.getVideoflyer().add(videoflyer);
        jumpday2.getVideoflyer().add(videoflyer);
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);

        VideoflyerDetailsDTO videoflyerDetails = videoflyerService.getById(videoflyer.getId());

        Assert.assertNotNull(videoflyerDetails);
        Assert.assertEquals(2, videoflyerDetails.getAssignments().size());
    }

    @Test
    public void testGetVideoflyerById_NotFound() {
        VideoflyerDetailsDTO videoflyerDetails = videoflyerService.getById("99999999999");
        Assert.assertNull(videoflyerDetails);
    }


    @Test
    public void testAssignVideoflyerToJumpday() {
        assignVideoflyer(ModelMockHelper.createJumpday());
    }

    @Test
    public void testAssignVideoflyerToJumpday_AlreadyAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String videoflyerId = assignVideoflyer(jumpday);

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                jumpday.getDate(), videoflyerId, true);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(1,assignedResult.getPayload().getVideoflyer().size());
        Assert.assertEquals(videoflyerId, assignedResult.getPayload().getVideoflyer().get(0).getId());
    }

    @Test
    public void testAssignVideoflyerToJumpday_VideoflyerNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                initialResult.getPayload().getDate(), "99999999", true);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    public void testAssignVideoflyerToJumpday_JumpdayNotFound() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), videoflyer.getId(), true);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testAssignVideoflyerToJumpday_Remove() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        String videoflyerId = assignVideoflyer(jumpday);

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyerId, false);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(0, assignedResult.getPayload().getVideoflyer().size());
    }

    @Test
    public void testAssignVideoflyerToJumpday_Remove_NotAssigned() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyer.getId(), false);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(jumpday.getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(0, assignedResult.getPayload().getVideoflyer().size());
    }

    private String assignVideoflyer(Jumpday jumpday) {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(jumpday);
        assertTrue(initialResult.isSuccess());
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                initialResult.getPayload().getDate(), videoflyer.getId(), true);
        GenericResult<Jumpday> assignedResult = jumpdayService.findJumpday(initialResult.getPayload().getDate());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(assignedResult.isSuccess());
        Assert.assertEquals(1, assignedResult.getPayload().getVideoflyer().size());
        Assert.assertEquals(videoflyer.getId(), assignedResult.getPayload().getVideoflyer().get(0).getId());

        return videoflyer.getId();
    }

    @Test
    public void testAssignVideoflyerToJumpday_Remove_VideoflyerNotFound() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                initialResult.getPayload().getDate(), "99999999", true);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.VIDEOFLYER_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    public void testAssignVideoflyerToJumpday_Remove_JumpdayNotFound() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        GenericResult<Void> result = videoflyerService.assignVideoflyerToJumpday(
                LocalDate.now().plus(1, ChronoUnit.YEARS), videoflyer.getId(), true);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testAssignVideoflyer_Addition() {
        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), true, LocalDate.now().plus(1, ChronoUnit.DAYS), true));

        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails);
        GenericResult<Jumpday> assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        GenericResult<Jumpday> assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(1, assignedResult1.getPayload().getVideoflyer().size());
        Assert.assertEquals(1, assignedResult2.getPayload().getVideoflyer().size());
    }

    private VideoflyerDetailsDTO prepareJumpdaysAndVideoflyer() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        jumpdayRepository.save(jumpday1);
        jumpdayRepository.save(jumpday2);
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        VideoflyerConverter converter = new VideoflyerConverter();
        return converter.convertToDetailsDto(videoflyer);
    }

    @Test
    public void testAssignVideoflyer_Removal() {
        VideoflyerDetailsDTO videoflyerDetails = prepareJumpdaysAndVideoflyer();
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now(), videoflyerDetails.getId(), true);
        videoflyerService.assignVideoflyerToJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS), videoflyerDetails.getId(), true);

        GenericResult<Jumpday> assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        GenericResult<Jumpday> assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        Assert.assertEquals(1, assignedResult1.getPayload().getVideoflyer().size());
        Assert.assertEquals(1, assignedResult2.getPayload().getVideoflyer().size());


        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), false, LocalDate.now().plus(1, ChronoUnit.DAYS), false));
        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails);

        assignedResult1 = jumpdayService.findJumpday(LocalDate.now());
        assignedResult2 = jumpdayService.findJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(0, assignedResult1.getPayload().getVideoflyer().size());
        Assert.assertEquals(0, assignedResult2.getPayload().getVideoflyer().size());
    }

    @Test
    public void testAssignVideoflyer_Error() {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        VideoflyerConverter converter = new VideoflyerConverter();
        VideoflyerDetailsDTO videoflyerDetails = converter.convertToDetailsDto(videoflyer);
        videoflyerDetails.setAssignments(Map.of(LocalDate.now(), false, LocalDate.now().plus(1, ChronoUnit.DAYS), false));

        GenericResult<Void> result = videoflyerService.assignVideoflyer(videoflyerDetails);

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }
}
