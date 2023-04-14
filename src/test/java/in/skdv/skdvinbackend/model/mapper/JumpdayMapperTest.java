package in.skdv.skdvinbackend.model.mapper;

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

class JumpdayMapperTest {

    private final JumpdayMapper mapper = new JumpdayMapperImpl(new AssignmentMapperImpl(new VideoflyerMapperImpl(), new TandemmasterMapperImpl()));
    private final ZoneId zoneId = ZoneId.of("Europe/Berlin");

    @Test
    void toDto() {
        Jumpday jumpday = createJumpday();

        JumpdayDTO jumpdayDTO = mapper.toDto(jumpday);

        assertEquals(jumpday.getDate(), jumpdayDTO.getDate());
        assertEquals(jumpday.getSlots(), jumpdayDTO.getSlots());
    }

    @Test
    void toDtoList() {
        Jumpday jumpday1 = createJumpday(LocalDate.now());
        Jumpday jumpday2 = createJumpday(LocalDate.now().plusDays(1));
        List<Jumpday> jumpdays = Arrays.asList(jumpday1, jumpday2);

        List<JumpdayDTO> jumpdayDTOList = mapper.toDto(jumpdays);

        assertEquals(jumpdays.size(), jumpdayDTOList.size());
        assertEquals(jumpday1.getDate(), jumpdayDTOList.get(0).getDate());
        assertEquals(jumpday2.getSlots(), jumpdayDTOList.get(1).getSlots());
    }

    @Test
    void toEntity() {
        Jumpday jumpday = createJumpday();
        JumpdayDTO jumpdayDTO = mapper.toDto(jumpday);

        jumpday = mapper.toEntity(jumpdayDTO, zoneId);

        assertEquals(jumpdayDTO.getDate(), jumpday.getDate());
        assertEquals(jumpdayDTO.getSlots(), jumpday.getSlots());
    }

    @Test
    void toDto_Null() {
        Jumpday jumpday = null;
        assertNull(mapper.toDto(jumpday));
    }

    @Test
    void toDtoList_Null() {
        List<Jumpday> jumpdays = null;
        assertEquals(0, mapper.toDto(jumpdays).size());
    }

    @Test
    void toEntity_Null() {
        assertNull(mapper.toEntity(null, null));
    }

    @Test
    void testtoDto_withAssignments() {
        Jumpday jumpday = createJumpday();
        jumpday.setTandemmaster(new ArrayList<>(Collections.singletonList(createAssignment(createTandemmaster()))));
        jumpday.setVideoflyer(new ArrayList<>(Collections.singletonList(createAssignment(createVideoflyer()))));

        JumpdayDTO jumpdayDTO = mapper.toDto(jumpday);

        assertTrue(jumpdayDTO.getTandemmaster().get(0) instanceof AssignmentDTO);
        assertTrue(jumpdayDTO.getTandemmaster().get(0).getFlyer() instanceof TandemmasterDTO);
        assertTrue(jumpdayDTO.getVideoflyer().get(0) instanceof AssignmentDTO);
        assertTrue(jumpdayDTO.getVideoflyer().get(0).getFlyer() instanceof VideoflyerDTO);
        assertEquals(jumpday.getTandemmaster().size(), jumpdayDTO.getTandemmaster().size());
        assertEquals(jumpday.getVideoflyer().size(), jumpdayDTO.getVideoflyer().size());
    }
}