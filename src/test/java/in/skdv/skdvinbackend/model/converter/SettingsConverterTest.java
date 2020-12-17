package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SettingsConverterTest {

    private SettingsConverter converter = new SettingsConverter();

    @Test
    public void convertToDto() {
        Settings settings = ModelMockHelper.createSettings();

        SettingsDTO settingsDTO = converter.convertToDto(settings);

        assertEquals(settings.getAdminSettings().getTandemCount(), settingsDTO.getAdminSettings().getTandemCount());
    }

    @Test
    public void convertToEntity() {
        Settings settings = ModelMockHelper.createSettings();

        SettingsDTO settingsDTO = converter.convertToDto(settings);
        settings = converter.convertToEntity(settingsDTO);

        assertEquals(settingsDTO.getAdminSettings().getTandemCount(), settings.getAdminSettings().getTandemCount());
    }

    @Test
    public void convertToDto_Null() {
        Settings settings = null;
        assertNull(converter.convertToDto(settings));
    }

    @Test
    public void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }
}