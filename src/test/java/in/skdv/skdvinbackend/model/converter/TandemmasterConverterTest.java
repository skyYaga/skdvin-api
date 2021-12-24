package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TandemmasterConverterTest {

    private TandemmasterConverter converter = new TandemmasterConverter();

    @Test
    void convertToDto() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();

        TandemmasterDTO tandemmasterDTO = converter.convertToDto(tandemmaster);

        assertEquals(tandemmaster.getFirstName(), tandemmasterDTO.getFirstName());
    }

    @Test
    void convertToDtoList() {
        Tandemmaster tandemmaster1 = ModelMockHelper.createTandemmaster();
        Tandemmaster tandemmaster2 = ModelMockHelper.createTandemmaster("John", "Doe");

        List<Tandemmaster> tandemmasters = Arrays.asList(tandemmaster1, tandemmaster2);

        List<TandemmasterDTO> tandemmasterDTOList = converter.convertToDto(tandemmasters);

        assertEquals(tandemmasters.size(), tandemmasterDTOList.size());
    }

    @Test
    void convertToEntity() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();

        TandemmasterDTO tandemmasterDTO = converter.convertToDto(tandemmaster);
        tandemmaster = converter.convertToEntity(tandemmasterDTO);

        assertEquals(tandemmasterDTO.getFirstName(), tandemmaster.getFirstName());
    }

    @Test
    void convertToDto_Null() {
        Tandemmaster tandemmaster = null;
        assertNull(converter.convertToDto(tandemmaster));
    }

    @Test
    void convertToDtoList_Null() {
        List<Tandemmaster> tandemmasters = null;
        assertEquals( 0, converter.convertToDto(tandemmasters).size());
    }

    @Test
    void convertToDetailsDto() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        TandemmasterDetailsDTO tandemmasterDTO = converter.convertToDetailsDto(tandemmaster, assignments);
        assertNotNull(tandemmasterDTO);
        assertEquals(tandemmaster.getFirstName(), tandemmasterDTO.getFirstName());
        assertEquals(assignments, tandemmasterDTO.getAssignments());
    }

    @Test
    void convertToDetailsDto_Null() {
        assertNull(converter.convertToDto((Tandemmaster) null));
    }
}