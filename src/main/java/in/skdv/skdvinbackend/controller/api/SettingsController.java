package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.SettingsConverter;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private ISettingsService settingsService;
    private SettingsRepository settingsRepository;
    private MessageSource messageSource;
    private SettingsConverter converter = new SettingsConverter();

    @Autowired
    public SettingsController(ISettingsService settingsService, SettingsRepository settingsRepository, MessageSource messageSource) {
        this.settingsService = settingsService;
        this.settingsRepository = settingsRepository;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_create:settings')")
    public ResponseEntity<GenericResult<SettingsDTO>> addSettings(@RequestBody @Valid SettingsDTO input) {
        Settings settings = settingsService.saveSettings(converter.convertToEntity(input));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResult<>(true, converter.convertToDto(settings)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:settings')")
    public ResponseEntity<GenericResult<SettingsDTO>> getSettings() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResult<>(true, converter.convertToDto(settingsService.getSettings())));
    }

    @GetMapping("/common")
    public ResponseEntity<GenericResult<CommonSettings>> getCommonSettings() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResult<>(true, settingsService.getCommonSettingsByLanguage(LocaleContextHolder.getLocale().getLanguage())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:settings')")
    public ResponseEntity<GenericResult<SettingsDTO>> updateSettings(@PathVariable String id, @RequestBody @Valid SettingsDTO input) {
        Optional<Settings> settings = settingsRepository.findById(id);

        if (settings.isPresent()) {
            input.setId(id);
            Settings savedSettings = settingsService.saveSettings(converter.convertToEntity(input));
            return ResponseEntity.ok(new GenericResult<>(true, converter.convertToDto(savedSettings)));

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(ErrorMessage.SETTINGS_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
    }

}
