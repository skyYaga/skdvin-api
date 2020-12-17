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

public class TandemmasterConverterTest {

    private TandemmasterConverter converter = new TandemmasterConverter();

    @Test
    public void convertToDto() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();

        TandemmasterDTO tandemmasterDTO = converter.convertToDto(tandemmaster);

        assertEquals(tandemmaster.getFirstName(), tandemmasterDTO.getFirstName());
    }

    @Test
    public void convertToDtoList() {
        Tandemmaster tandemmaster1 = ModelMockHelper.createTandemmaster();
        Tandemmaster tandemmaster2 = ModelMockHelper.createTandemmaster("John", "Doe");

        List<Tandemmaster> tandemmasters = Arrays.asList(tandemmaster1, tandemmaster2);

        List<TandemmasterDTO> tandemmasterDTOList = converter.convertToDto(tandemmasters);

        assertEquals(tandemmasters.size(), tandemmasterDTOList.size());
    }

    @Test
    public void convertToEntity() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();

        TandemmasterDTO tandemmasterDTO = converter.convertToDto(tandemmaster);
        tandemmaster = converter.convertToEntity(tandemmasterDTO);

        assertEquals(tandemmasterDTO.getFirstName(), tandemmaster.getFirstName());
    }

    @Test
    public void convertToDto_Null() {
        Tandemmaster tandemmaster = null;
        assertNull(converter.convertToDto(tandemmaster));
    }

    @Test
    public void convertToDtoList_Null() {
        List<Tandemmaster> tandemmasters = null;
        assertEquals( 0, converter.convertToDto(tandemmasters).size());
    }

    @Test
    public void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }

    @Test
    public void convertToDetailsDto() {
        Tandemmaster tandemmaster = ModelMockHelper.createTandemmaster();
        Map<LocalDate, SimpleAssignment> assignments = Map.of(LocalDate.now(), new SimpleAssignment(true));
        TandemmasterDetailsDTO tandemmasterDTO = converter.convertToDetailsDto(tandemmaster, assignments);
        assertNotNull(tandemmasterDTO);
        assertEquals(tandemmaster.getFirstName(), tandemmasterDTO.getFirstName());
        assertEquals(assignments, tandemmasterDTO.getAssignments());
    }

    @Test
    public void convertToDetailsDto_Null() {
        assertNull(converter.convertToDto((Tandemmaster) null));
    }
}