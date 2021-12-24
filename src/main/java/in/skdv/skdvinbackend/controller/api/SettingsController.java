package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.converter.SettingsConverter;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.dto.SettingsInputDTO;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.model.entity.settings.WaiverSettings;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.util.GenericResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final ISettingsService settingsService;
    private final SettingsConverter converter = new SettingsConverter();

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_create:settings')")
    public ResponseEntity<GenericResult<SettingsDTO>> addSettings(@RequestBody @Valid SettingsDTO input) {
        log.info("Adding settings {}", input);
        Settings settings = settingsService.saveSettings(converter.convertToEntity(input));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResult<>(true, converter.convertToDto(settings)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:settings')")
    public GenericResult<SettingsDTO> getSettings() {
        log.info("Getting settings");
        return new GenericResult<>(true, converter.convertToDto(settingsService.getSettings()));
    }

    @GetMapping("/common")
    @PreAuthorize("permitAll")
    public GenericResult<CommonSettings> getCommonSettings() {
        log.info("Getting common settings");
        return new GenericResult<>(true, settingsService.getCommonSettingsByLanguage(LocaleContextHolder.getLocale().getLanguage()));
    }

    @GetMapping("/waiver")
    @PreAuthorize("permitAll")
    public GenericResult<WaiverSettings> getWaiverSettings() {
        log.info("Getting waiver settings");
        return new GenericResult<>(true, settingsService.getWaiverSettingsByLanguage(LocaleContextHolder.getLocale().getLanguage()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:settings')")
    public GenericResult<SettingsDTO> updateSettings(@PathVariable String id, @RequestBody @Valid SettingsInputDTO input) {
        log.info("Updating settings {}: {}", id, input);
        Settings savedSettings = settingsService.updateSettings(converter.convertToEntity(id, input));
        return new GenericResult<>(true, converter.convertToDto(savedSettings));
    }

}
