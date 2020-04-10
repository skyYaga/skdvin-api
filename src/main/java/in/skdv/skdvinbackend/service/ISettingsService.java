package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;

import java.util.Locale;
import java.util.Map;

public interface ISettingsService {

    Settings saveSettings(Settings settings);

    Settings getSettings();

    AdminSettings getAdminSettings();

    Map<Locale, CommonSettings> getCommonSettings();

    CommonSettings getCommonSettingsByLocale(Locale locale);
}
