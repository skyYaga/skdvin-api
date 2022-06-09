package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SettingsMapperTest {

    private final SettingsMapper mapper = new SettingsMapperImpl();

    @Test
    void toDto() {
        Settings settings = ModelMockHelper.createSettings();

        SettingsDTO settingsDTO = mapper.toDto(settings);

        assertEquals(settings.getAdminSettings().getTandemCount(), settingsDTO.getAdminSettings().getTandemCount());
    }

    @Test
    void toEntity() {
        Settings settings = ModelMockHelper.createSettings();

        SettingsDTO settingsDTO = mapper.toDto(settings);
        settings = mapper.toEntity(settingsDTO);

        assertEquals(settingsDTO.getAdminSettings().getTandemCount(), settings.getAdminSettings().getTandemCount());
    }

    @Test
    void toDto_Null() {
        Settings settings = null;
        assertNull(mapper.toDto(settings));
    }

    @Test
    void toEntity_Null() {
        assertNull(mapper.toEntity(null));
    }
}