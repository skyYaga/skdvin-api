package in.skdv.skdvinbackend.model.mapper;

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

class VideoflyerMapperTest {

    private final VideoflyerMapper mapper = new VideoflyerMapperImpl();

    @Test
    void toDto() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();
        videoflyer.setFavorite(true);

        VideoflyerDTO videoflyerDTO = mapper.toDto(videoflyer);

        assertEquals(videoflyer.getFirstName(), videoflyerDTO.getFirstName());
        assertEquals(videoflyer.isFavorite(), videoflyerDTO.isFavorite());
    }

    @Test
    void toDtoList() {
        Videoflyer videoflyer1 = ModelMockHelper.createVideoflyer();
        Videoflyer videoflyer2 = ModelMockHelper.createVideoflyer("John", "Doe");

        List<Videoflyer> videoflyers = Arrays.asList(videoflyer1, videoflyer2);

        List<VideoflyerDTO> videoflyerDTOList = mapper.toDto(videoflyers);

        assertEquals(videoflyers.size(), videoflyerDTOList.size());
    }

    @Test
    void toEntity() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();

        VideoflyerDTO videoflyerDTO = mapper.toDto(videoflyer);
        videoflyer = mapper.toEntity(videoflyerDTO);

        assertEquals(videoflyerDTO.getFirstName(), videoflyer.getFirstName());
    }

    @Test
    void toDto_Null() {
        Videoflyer videoflyer = null;
        assertNull(mapper.toDto(videoflyer));
    }

    @Test
    void toDtoList_Null() {
        List<Videoflyer> videoflyers = null;
        assertEquals( 0, mapper.toDto(videoflyers).size());
    }

    @Test
    void toDetailsDto() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        VideoflyerDetailsDTO videoflyerDTO = mapper.toDetailsDto(videoflyer, assignments);
        assertNotNull(videoflyerDTO);
        assertEquals(videoflyer.getFirstName(), videoflyerDTO.getFirstName());
        assertEquals(assignments, videoflyerDTO.getAssignments());
    }

    @Test
    void toDetailsDto_Null() {
        assertNull(mapper.toDto((Videoflyer) null));
    }

    @Test
    void convertFromDetails() {
        Videoflyer videoflyer = ModelMockHelper.createVideoflyer();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        VideoflyerDetails details = mapper.toDetails(videoflyer, assignments);

        Videoflyer convertedFromDetails = mapper.fromDetails(details);

        assertNotNull(convertedFromDetails);
        assertEquals(videoflyer, convertedFromDetails);
    }

    @Test
    void convertFromDetails_null() {
        assertNull(mapper.fromDetails(null));
    }
}