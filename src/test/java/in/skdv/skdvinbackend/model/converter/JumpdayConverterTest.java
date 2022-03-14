package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.AssignmentDTO;
import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static in.skdv.skdvinbackend.ModelMockHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class JumpdayConverterTest {

    private final JumpdayConverter converter = new JumpdayConverter(ZoneId.of("Europe/Berlin"));

    @Test
    void convertToDto() {
        Jumpday jumpday = createJumpday();

        JumpdayDTO jumpdayDTO = converter.convertToDto(jumpday);

        assertEquals(jumpday.getDate(), jumpdayDTO.getDate());
        assertEquals(jumpday.getSlots(), jumpdayDTO.getSlots());
    }

    @Test
    void convertToDtoList() {
        Jumpday jumpday1 = createJumpday(LocalDate.now());
        Jumpday jumpday2 = createJumpday(LocalDate.now().plusDays(1));
        List<Jumpday> jumpdays = Arrays.asList(jumpday1, jumpday2);

        List<JumpdayDTO> jumpdayDTOList = converter.convertToDto(jumpdays);

        assertEquals(jumpdays.size(), jumpdayDTOList.size());
        assertEquals(jumpday1.getDate(), jumpdayDTOList.get(0).getDate());
        assertEquals(jumpday2.getSlots(), jumpdayDTOList.get(1).getSlots());
    }

    @Test
    void convertToEntity() {
        Jumpday jumpday = createJumpday();
        JumpdayDTO jumpdayDTO = converter.convertToDto(jumpday);

        jumpday = converter.convertToEntity(jumpdayDTO);

        assertEquals(jumpdayDTO.getDate(), jumpday.getDate());
        assertEquals(jumpdayDTO.getSlots(), jumpday.getSlots());
    }

    @Test
    void convertToDto_Null() {
        Jumpday jumpday = null;
        assertNull(converter.convertToDto(jumpday));
    }

    @Test
    void convertToDtoList_Null() {
        List<Jumpday> jumpdays = null;
        assertEquals(0, converter.convertToDto(jumpdays).size());
    }

    @Test
    void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }

    @Test
    void testConvertToDto_withAssignments() {
        Jumpday jumpday = createJumpday();
        jumpday.setTandemmaster(new ArrayList<>(Collections.singletonList(createAssignment(createTandemmaster()))));
        jumpday.setVideoflyer(new ArrayList<>(Collections.singletonList(createAssignment(createVideoflyer()))));

        JumpdayDTO jumpdayDTO = converter.convertToDto(jumpday);

        assertTrue(jumpdayDTO.getTandemmaster().get(0) instanceof AssignmentDTO);
        assertTrue(jumpdayDTO.getTandemmaster().get(0).getFlyer() instanceof TandemmasterDTO);
        assertTrue(jumpdayDTO.getVideoflyer().get(0) instanceof AssignmentDTO);
        assertTrue(jumpdayDTO.getVideoflyer().get(0).getFlyer() instanceof VideoflyerDTO);
        assertEquals(jumpday.getTandemmaster().size(), jumpdayDTO.getTandemmaster().size());
        assertEquals(jumpday.getVideoflyer().size(), jumpdayDTO.getVideoflyer().size());
    }
}