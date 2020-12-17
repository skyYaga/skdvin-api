package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SettingsConverterTest {

    private SettingsConverter converter = new SettingsConverter();

    @Test
    void convertToDto() {
        Settings settings = ModelMockHelper.createSettings();

        SettingsDTO settingsDTO = converter.convertToDto(settings);

        assertEquals(settings.getAdminSettings().getTandemCount(), settingsDTO.getAdminSettings().getTandemCount());
    }

    @Test
    void convertToEntity() {
        Settings settings = ModelMockHelper.createSettings();

        SettingsDTO settingsDTO = converter.convertToDto(settings);
        settings = converter.convertToEntity(settingsDTO);

        assertEquals(settingsDTO.getAdminSettings().getTandemCount(), settings.getAdminSettings().getTandemCount());
    }

    @Test
    void convertToDto_Null() {
        Settings settings = null;
        assertNull(converter.convertToDto(settings));
    }

    @Test
    void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }
}