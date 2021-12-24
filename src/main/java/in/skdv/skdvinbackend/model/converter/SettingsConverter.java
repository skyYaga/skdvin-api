package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.dto.SettingsInputDTO;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import org.modelmapper.ModelMapper;

public class SettingsConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public SettingsDTO convertToDto(Settings settings) {
        if (settings == null) {
            return null;
        }
        return modelMapper.map(settings, SettingsDTO.class);
    }

    public Settings convertToEntity(SettingsDTO settingsDTO) {
        if (settingsDTO == null) {
            return null;
        }
        return modelMapper.map(settingsDTO, Settings.class);
    }

    public Settings convertToEntity(String id, SettingsInputDTO input) {
        Settings settings = modelMapper.map(input, Settings.class);
        settings.setId(id);
        return settings;
    }
}
