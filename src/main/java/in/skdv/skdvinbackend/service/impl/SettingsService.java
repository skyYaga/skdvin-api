package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.domain.PublicSettings;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService implements ISettingsService {

    private final SettingsRepository settingsRepository;

    @Override
    @Transactional
    public Settings saveSettings(Settings settings) {
        Settings existingSettings = getSettings();
        if (existingSettings != null) {
            settings.setId(existingSettings.getId());
        }
        return settingsRepository.save(settings);
    }

    @Override
    @Transactional
    public Settings updateSettings(Settings input) {
        Optional<Settings> settings = settingsRepository.findById(input.getId());

        if (settings.isEmpty()) {
            log.error("Settings {} not found.", input.getId());
            throw new NotFoundException(ErrorMessage.SETTINGS_NOT_FOUND);
        }

        return settingsRepository.save(input);
    }

    @Override
    public Settings getSettings() {
        return settingsRepository.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public Settings findById(String id) {
        Optional<Settings> settings = settingsRepository.findById(id);

        if (settings.isEmpty()) {
            log.error("Settings {} not found.", id);
            throw new NotFoundException(ErrorMessage.SETTINGS_NOT_FOUND);
        }
        return settings.get();
    }

    @Override
    public AdminSettings getAdminSettings() {
        Settings settings = getSettings();
        if (settings != null) {
            return settings.getAdminSettings();
        }
        return null;
    }

    @Override
    public Map<String, LanguageSettings> getLanguageSettings() {
        Settings settings = getSettings();
        if (settings != null) {
            return settings.getLanguageSettings();
        }
        return new HashMap<>();
    }

    @Override
    public PublicSettings getPublicSettingsByLanguage(String language) {
        Settings settings = getSettings();
        LanguageSettings languageSettings = settings.getLanguageSettingsByLocaleOrDefault(language);
        return new PublicSettings(settings.getCommonSettings(), languageSettings);
    }

}
