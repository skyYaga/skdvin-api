package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoJumpdayServiceTest {

    @Autowired
    JumpdayRepository jumpdayRepository;

    @Autowired
    IJumpdayService jumpdayService;

    @Before
    public void setup() {
        jumpdayRepository.deleteAll();
    }

    @Test
    public void testSaveJumpday() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        GenericResult<Jumpday> savedJumpday = jumpdayService.saveJumpday(jumpday);
        assertNotNull(savedJumpday);
        assertTrue(savedJumpday.isSuccess());
        assertNotNull(savedJumpday.getPayload().get_id());
        assertEquals(jumpday.getDate(), savedJumpday.getPayload().getDate());
        assertTrue(savedJumpday.getPayload().isJumping());
        assertEquals(1, savedJumpday.getPayload().getSlots().size());
        assertEquals(jumpday.getSlots().get(0).getTime(), savedJumpday.getPayload().getSlots().get(0).getTime());
        assertEquals(4, savedJumpday.getPayload().getSlots().get(0).getTandemTotal());
        assertEquals(2, savedJumpday.getPayload().getSlots().get(0).getVideoTotal());
        assertEquals(1, savedJumpday.getPayload().getTandemmaster().size());
        assertEquals(1, savedJumpday.getPayload().getVideoflyer().size());
    }

    @Test
    public void testFindJumpdayByDate() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        GenericResult<Jumpday> result = jumpdayService.saveJumpday(jumpday);
        assertTrue(result.isSuccess());

        GenericResult<Jumpday> foundJumpday = jumpdayService.findJumpday(jumpday.getDate());
        assertNotNull(foundJumpday);
        assertTrue(foundJumpday.isSuccess());
        assertEquals(jumpday.getDate(), foundJumpday.getPayload().getDate());
    }

    @Test
    public void testFindJumpdayByDate_NotFound() {
        GenericResult<Jumpday> foundJumpday = jumpdayService.findJumpday(LocalDate.now());
        assertNotNull(foundJumpday);
        assertFalse(foundJumpday.isSuccess());
        assertEquals("jumpday.not.found", foundJumpday.getMessage());
    }


    @Test
    public void testFindJumpdays() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday();
        jumpday2.setDate(LocalDate.now().plusDays(1));

        GenericResult<Jumpday> result1 = jumpdayService.saveJumpday(jumpday1);
        assertTrue(result1.isSuccess());
        GenericResult<Jumpday> result2 = jumpdayService.saveJumpday(jumpday2);
        assertTrue(result2.isSuccess());

        GenericResult<List<Jumpday>> jumpdays = jumpdayService.findJumpdays();
        assertNotNull(jumpdays);
        assertTrue(jumpdays.isSuccess());
        assertEquals(2, jumpdays.getPayload().size());
    }

    @Test
    public void testFindJumpdays_empty() {
        GenericResult<List<Jumpday>> jumpdays = jumpdayService.findJumpdays();
        assertNotNull(jumpdays);
        assertTrue(jumpdays.isSuccess());
        assertEquals(0, jumpdays.getPayload().size());
    }
}
