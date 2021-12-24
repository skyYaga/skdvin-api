package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.model.entity.settings.WaiverSettings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SettingsService implements ISettingsService {

    private static final Locale DEFAULT_LOCALE = Locale.GERMAN;
    private final SettingsRepository settingsRepository;

    @Override
    public Settings saveSettings(Settings settings) {
        Settings existingSettings = getSettings();
        if (existingSettings != null) {
            settings.setId(existingSettings.getId());
        }
        return settingsRepository.save(settings);
    }

    @Override
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
    public Map<String, CommonSettings> getCommonSettings() {
        Settings settings = getSettings();
        if (settings != null) {
            return settings.getCommonSettings();
        }
        return new HashMap<>();
    }

    @Override
    public CommonSettings getCommonSettingsByLanguage(String language) {
        Map<String, CommonSettings> commonSettings = getSettings().getCommonSettings();
        return getCommonSettingsByLocaleOrDefault(commonSettings, language);
    }

    @Override
    public Map<String, WaiverSettings> getWaiverSettings() {
        Settings settings = getSettings();
        if (settings != null) {
            return settings.getWaiverSettings();
        }
        return new HashMap<>();
    }

    @Override
    public WaiverSettings getWaiverSettingsByLanguage(String language) {
        Map<String, WaiverSettings> waiverSettings = getSettings().getWaiverSettings();
        return getWaiverSettingsByLocaleOrDefault(waiverSettings, language);
    }

    private CommonSettings getCommonSettingsByLocaleOrDefault(Map<String, CommonSettings> commonSettings, String language) {
        if (commonSettings != null) {
            CommonSettings localeCommonSettings = commonSettings.get(language);
            if (localeCommonSettings == null) {
                localeCommonSettings = commonSettings.get(DEFAULT_LOCALE.getLanguage());
            }
            return localeCommonSettings;
        }
        return null;
    }

    private WaiverSettings getWaiverSettingsByLocaleOrDefault(Map<String, WaiverSettings> waiverSettings, String language) {
        if (waiverSettings != null) {
            WaiverSettings localeWaiverSettings = waiverSettings.get(language);
            if (localeWaiverSettings == null) {
                localeWaiverSettings = waiverSettings.get(DEFAULT_LOCALE.getLanguage());
            }
            return localeWaiverSettings;
        }
        return null;
    }

}
