package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.controller.api.response.PublicSettingsResponse;
import in.skdv.skdvinbackend.model.domain.PublicSettings;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.dto.SettingsInputDTO;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.model.mapper.SettingsMapper;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.util.GenericResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final ISettingsService settingsService;
    private final SettingsMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_create:settings')")
    public GenericResult<SettingsDTO> addSettings(@RequestBody @Valid SettingsDTO input) {
        log.info("Adding settings {}", input);
        Settings settings = settingsService.saveSettings(mapper.toEntity(input));
        return new GenericResult<>(true, mapper.toDto(settings));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:settings')")
    public GenericResult<SettingsDTO> getSettings() {
        log.info("Getting settings");
        return new GenericResult<>(true, mapper.toDto(settingsService.getSettings()));
    }

    @GetMapping("/common")
    @PreAuthorize("permitAll")
    public GenericResult<PublicSettingsResponse> getCommonSettings() {
        log.info("Getting public settings");
        PublicSettings publicSettings = settingsService.getPublicSettingsByLanguage(LocaleContextHolder.getLocale().getLanguage());
        PublicSettingsResponse response = mapper.toResponse(publicSettings);
        return new GenericResult<>(true, response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:settings')")
    public GenericResult<SettingsDTO> updateSettings(@PathVariable String id, @RequestBody @Valid SettingsInputDTO input) {
        log.info("Updating settings {}: {}", id, input);
        Settings savedSettings = settingsService.updateSettings(mapper.toEntity(id, input));
        return new GenericResult<>(true, mapper.toDto(savedSettings));
    }

}
