package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VideoflyerConverterTest {

    private VideoflyerConverter converter = new VideoflyerConverter();

    @Test
    void convertToDto() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();

        VideoflyerDTO videoflyerDTO = converter.convertToDto(videoflyer);

        assertEquals(videoflyer.getFirstName(), videoflyerDTO.getFirstName());
    }

    @Test
    void convertToDtoList() {
        Videoflyer videoflyer1 = ModelMockHelper.createVideoflyer();
        Videoflyer videoflyer2 = ModelMockHelper.createVideoflyer("John", "Doe");

        List<Videoflyer> videoflyers = Arrays.asList(videoflyer1, videoflyer2);

        List<VideoflyerDTO> videoflyerDTOList = converter.convertToDto(videoflyers);

        assertEquals(videoflyers.size(), videoflyerDTOList.size());
    }

    @Test
    void convertToEntity() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();

        VideoflyerDTO videoflyerDTO = converter.convertToDto(videoflyer);
        videoflyer = converter.convertToEntity(videoflyerDTO);

        assertEquals(videoflyerDTO.getFirstName(), videoflyer.getFirstName());
    }

    @Test
    void convertToDto_Null() {
        Videoflyer videoflyer = null;
        assertNull(converter.convertToDto(videoflyer));
    }

    @Test
    void convertToDtoList_Null() {
        List<Videoflyer> videoflyers = null;
        assertEquals( 0, converter.convertToDto(videoflyers).size());
    }

    @Test
    void convertToDetailsDto() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        VideoflyerDetailsDTO videoflyerDTO = converter.convertToDetailsDto(videoflyer, assignments);
        assertNotNull(videoflyerDTO);
        assertEquals(videoflyer.getFirstName(), videoflyerDTO.getFirstName());
        assertEquals(assignments, videoflyerDTO.getAssignments());
    }

    @Test
    void convertToDetailsDto_Null() {
        assertNull(converter.convertToDto((Videoflyer) null));
    }

    @Test
    void convertFromDetails() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        VideoflyerDetails details = converter.convertToDetails(videoflyer, assignments);

        Videoflyer convertedFromDetails = converter.convertFromDetails(details);

        assertNotNull(convertedFromDetails);
        assertEquals(videoflyer, convertedFromDetails);
    }

    @Test
    void convertFromDetails_null() {
        assertNull(converter.convertFromDetails(null));
    }
}