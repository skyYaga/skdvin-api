package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WaiverConverterTest {

    private WaiverConverter converter = new WaiverConverter();

    @Test
    void convertToDto() {
        Waiver waiver = ModelMockHelper.createWaiver();

        WaiverDTO waiverDTO = converter.convertToDto(waiver);

        assertEquals(waiver.getWaiverCustomer().getStreet(), waiverDTO.getWaiverCustomer().getStreet());
    }

    @Test
    void convertToDtoList() {
        Waiver waiver1 = ModelMockHelper.createWaiver();
        Waiver waiver2 = ModelMockHelper.createWaiver();
        List<Waiver> waivers = Arrays.asList(waiver1, waiver2);

        List<WaiverDTO> waiverDTOList = converter.convertToDto(waivers);

        assertEquals(waivers.size(), waiverDTOList.size());
    }

    @Test
    void convertToEntity() {
        Waiver waiver = ModelMockHelper.createWaiver();

        WaiverDTO waiverDTO = converter.convertToDto(waiver);
        waiver = converter.convertToEntity(waiverDTO);

        assertEquals(waiverDTO.getWaiverCustomer().getStreet(), waiver.getWaiverCustomer().getStreet());
    }

    @Test
    void convertToDto_Null() {
        Waiver waiver = null;
        assertNull(converter.convertToDto(waiver));
    }

    @Test
    void convertToDtoList_Null() {
        List<Waiver> waivers = null;
        assertEquals( 0, converter.convertToDto(waivers).size());
    }

    @Test
    void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }
}