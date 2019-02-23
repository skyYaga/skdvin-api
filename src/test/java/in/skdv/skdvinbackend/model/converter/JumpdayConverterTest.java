package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JumpdayConverterTest {

    private JumpdayConverter converter = new JumpdayConverter();

    @Test
    public void convertToDto() {
        Jumpday jumpday = ModelMockHelper.createJumpday();

        JumpdayDTO jumpdayDTO = converter.convertToDto(jumpday);

        assertEquals(jumpday.getDate(), jumpdayDTO.getDate());
        assertEquals(jumpday.getSlots(), jumpdayDTO.getSlots());
        assertEquals(jumpday.getTandemmaster(), jumpdayDTO.getTandemmaster());
        assertEquals(jumpday.getVideoflyer(), jumpdayDTO.getVideoflyer());
    }

    @Test
    public void convertToDtoList() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday(LocalDate.now());
        Jumpday jumpday2 = ModelMockHelper.createJumpday(LocalDate.now().plusDays(1));
        List<Jumpday> jumpdays = Arrays.asList(jumpday1, jumpday2);

        List<JumpdayDTO> jumpdayDTOList = converter.convertToDto(jumpdays);

        assertEquals(jumpdays.size(), jumpdayDTOList.size());
        assertEquals(jumpday1.getDate(), jumpdayDTOList.get(0).getDate());
        assertEquals(jumpday2.getSlots(), jumpdayDTOList.get(1).getSlots());
    }

    @Test
    public void convertToEntity() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        JumpdayDTO jumpdayDTO = converter.convertToDto(jumpday);

        jumpday = converter.convertToEntity(jumpdayDTO);

        assertEquals(jumpdayDTO.getDate(), jumpday.getDate());
        assertEquals(jumpdayDTO.getSlots(), jumpday.getSlots());
        assertEquals(jumpdayDTO.getTandemmaster(), jumpday.getTandemmaster());
        assertEquals(jumpdayDTO.getVideoflyer(), jumpday.getVideoflyer());

    }

    @Test
    public void convertToDto_Null() {
        Jumpday jumpday = null;
        assertNull(converter.convertToDto(jumpday));
    }

    @Test
    public void convertToDtoList_Null() {
        List<Jumpday> jumpdays = null;
        assertEquals( 0, converter.convertToDto(jumpdays).size());
    }

    @Test
    public void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }
}