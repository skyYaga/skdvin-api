package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.TandemmasterDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TandemmasterMapperTest {

    private final TandemmasterMapper mapper = new TandemmasterMapperImpl();

    @Test
    void toDto() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        tandemmaster.setFavorite(true);

        TandemmasterDTO tandemmasterDTO = mapper.toDto(tandemmaster);

        assertEquals(tandemmaster.getFirstName(), tandemmasterDTO.getFirstName());
        assertEquals(tandemmaster.isFavorite(), tandemmasterDTO.isFavorite());
    }

    @Test
    void toDtoList() {
        Tandemmaster tandemmaster1 = ModelMockHelper.createTandemmaster();
        Tandemmaster tandemmaster2 = ModelMockHelper.createTandemmaster("John", "Doe");

        List<Tandemmaster> tandemmasters = Arrays.asList(tandemmaster1, tandemmaster2);

        List<TandemmasterDTO> tandemmasterDTOList = mapper.toDto(tandemmasters);

        assertEquals(tandemmasters.size(), tandemmasterDTOList.size());
    }

    @Test
    void toEntity() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();

        TandemmasterDTO tandemmasterDTO = mapper.toDto(tandemmaster);
        tandemmaster = mapper.toEntity(tandemmasterDTO);

        assertEquals(tandemmasterDTO.getFirstName(), tandemmaster.getFirstName());
    }

    @Test
    void toDto_Null() {
        Tandemmaster tandemmaster = null;
        assertNull(mapper.toDto(tandemmaster));
    }

    @Test
    void toDtoList_Null() {
        List<Tandemmaster> tandemmasters = null;
        assertEquals(0, mapper.toDto(tandemmasters).size());
    }

    @Test
    void toDetailsDto() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        TandemmasterDetailsDTO tandemmasterDTO = mapper.toDetailsDto(tandemmaster, assignments);
        assertNotNull(tandemmasterDTO);
        assertEquals(tandemmaster.getFirstName(), tandemmasterDTO.getFirstName());
        assertEquals(assignments, tandemmasterDTO.getAssignments());
    }

    @Test
    void toDetailsDto_Null() {
        assertNull(mapper.toDto((Tandemmaster) null));
    }

    @Test
    void convertFromDetails() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        TandemmasterDetails details = mapper.toDetails(tandemmaster, assignments);

        Tandemmaster convertedFromDetails = mapper.fromDetails(details);

        assertNotNull(convertedFromDetails);
        assertEquals(tandemmaster, convertedFromDetails);
    }

    @Test
    void convertFromDetails_null() {
        assertNull(mapper.fromDetails(null));
    }
}