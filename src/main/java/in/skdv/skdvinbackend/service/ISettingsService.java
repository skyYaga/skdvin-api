package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.domain.PublicSettings;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;

import java.util.Map;

public interface ISettingsService {

    Settings saveSettings(Settings settings);

    Settings updateSettings(Settings settings);

    Settings getSettings();

    Settings findById(String id);

    AdminSettings getAdminSettings();

    Map<String, LanguageSettings> getLanguageSettings();

    PublicSettings getPublicSettingsByLanguage(String language);
}
