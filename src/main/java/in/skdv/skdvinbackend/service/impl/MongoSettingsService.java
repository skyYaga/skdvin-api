package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MongoSettingsService implements ISettingsService {

    private static final Locale DEFAULT_LOCALE = Locale.GERMAN;
    private SettingsRepository settingsRepository;

    @Autowired
    public MongoSettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public List<Settings> saveSettings(List<Settings> settings) {
        return settingsRepository.saveAll(settings);
    }

    @Override
    public List<Settings> getSettings() {
        return settingsRepository.findAll();
    }

    @Override
    public Settings getSettingsByLocale(Locale locale) {
        Settings settings = settingsRepository.findByLocale(locale);
        if (settings == null) {
            settings = settingsRepository.findByLocale(DEFAULT_LOCALE);
        }
        return settings;
    }

    @Override
    public List<AdminSettings> getAdminSettings() {
        List<Settings> settings = getSettings();
        List<AdminSettings> adminSettings = new ArrayList<>();

        settings.forEach(s -> adminSettings.add(s.getAdminSettings()));

        return adminSettings;
    }

    @Override
    public List<CommonSettings> getCommonSettings() {
        List<Settings> settings = getSettings();
        List<CommonSettings> commonSettings = new ArrayList<>();

        settings.forEach(s -> commonSettings.add(s.getCommonSettings()));

        return commonSettings;
    }

    @Override
    public CommonSettings getCommonSettingsByLocale(Locale locale) {
        return getSettingsByLocale(locale).getCommonSettings();
    }

    @Override
    public AdminSettings getAdminSettingsByLocale(Locale locale) {
        return getSettingsByLocale(locale).getAdminSettings();
    }
}
