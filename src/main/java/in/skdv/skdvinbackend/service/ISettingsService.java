package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;

import java.util.List;
import java.util.Locale;

public interface ISettingsService {

    List<Settings> saveSettings(List<Settings> settings);

    List<Settings> getSettings();

    Settings getSettingsByLocale(Locale locale);

    List<AdminSettings> getAdminSettings();

    List<CommonSettings> getCommonSettings();

    CommonSettings getCommonSettingsByLocale(Locale locale);

    AdminSettings getAdminSettingsByLocale(Locale locale);
}
