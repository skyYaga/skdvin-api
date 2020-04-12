package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MongoSettingsService implements ISettingsService {

    private static final Locale DEFAULT_LOCALE = Locale.GERMAN;
    private SettingsRepository settingsRepository;

    @Autowired
    public MongoSettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Settings saveSettings(Settings settings) {
        Settings existingSettings = getSettings();
        if (existingSettings != null) {
            settings.setId(existingSettings.getId());
        }
        return settingsRepository.save(settings);
    }

    @Override
    public Settings getSettings() {
        return settingsRepository.findAll().stream().findFirst().orElse(null);
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

}
