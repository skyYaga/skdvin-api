package in.skdv.skdvinbackend.model.mapper;

import in.skdv.skdvinbackend.controller.api.response.PublicSettingsResponse;
import in.skdv.skdvinbackend.model.domain.PublicSettings;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.dto.SettingsInputDTO;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SettingsMapper {
    SettingsDTO toDto(Settings settings);

    Settings toEntity(SettingsDTO input);

    Settings toEntity(String id, SettingsInputDTO input);

    @Mapping(target = ".", source = "commonSettings")
    @Mapping(target = ".", source = "languageSettings")
    PublicSettingsResponse toResponse(PublicSettings publicSettings);
}
