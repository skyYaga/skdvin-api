package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VideoflyerConverterTest {

    private VideoflyerConverter converter = new VideoflyerConverter();

    @Test
    public void convertToDto() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();

        VideoflyerDTO videoflyerDTO = converter.convertToDto(videoflyer);

        assertEquals(videoflyer.getFirstName(), videoflyerDTO.getFirstName());
    }

    @Test
    public void convertToDtoList() {
        Videoflyer videoflyer1 = ModelMockHelper.createVideoflyer();
        Videoflyer videoflyer2 = ModelMockHelper.createVideoflyer("John", "Doe");

        List<Videoflyer> videoflyers = Arrays.asList(videoflyer1, videoflyer2);

        List<VideoflyerDTO> videoflyerDTOList = converter.convertToDto(videoflyers);

        assertEquals(videoflyers.size(), videoflyerDTOList.size());
    }

    @Test
    public void convertToEntity() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();

        VideoflyerDTO videoflyerDTO = converter.convertToDto(videoflyer);
        videoflyer = converter.convertToEntity(videoflyerDTO);

        assertEquals(videoflyerDTO.getFirstName(), videoflyer.getFirstName());
    }

    @Test
    public void convertToDto_Null() {
        Videoflyer videoflyer = null;
        assertNull(converter.convertToDto(videoflyer));
    }

    @Test
    public void convertToDtoList_Null() {
        List<Videoflyer> videoflyers = null;
        assertEquals( 0, converter.convertToDto(videoflyers).size());
    }

    @Test
    public void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }

    @Test
    public void convertToDetailsDto() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        VideoflyerDetailsDTO videoflyerDTO = converter.convertToDetailsDto(videoflyer, assignments);
        assertNotNull(videoflyerDTO);
        assertEquals(videoflyer.getFirstName(), videoflyerDTO.getFirstName());
        assertEquals(assignments, videoflyerDTO.getAssignments());
    }

    @Test
    public void convertToDetailsDto_Null() {
        assertNull(converter.convertToDto((Videoflyer) null));
    }
}