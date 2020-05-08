package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;

import java.util.Map;

public interface ISettingsService {

    Settings saveSettings(Settings settings);

    Settings getSettings();

    AdminSettings getAdminSettings();

    Map<String, CommonSettings> getCommonSettings();

    CommonSettings getCommonSettingsByLanguage(String language);
}