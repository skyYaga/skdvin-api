package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.domain.PublicSettings;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SettingsServiceTest extends AbstractSkdvinTest {

    @Autowired
    private ISettingsService settingsService;

    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    void setup() {
        settingsRepository.deleteAll();
    }

    @Test
    void testAddSettings() {
        Settings settings = ModelMockHelper.createSettings();

        Settings saved = settingsService.saveSettings(settings);

        assertNotNull(saved.getId());
        assertEquals(5, saved.getAdminSettings().getTandemCount());
        assertEquals("Example DZ", saved.getLanguageSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getName());
    }

    @Test
    void testAddSettings_OverridesFirst() {
        settingsService.saveSettings(ModelMockHelper.createSettings());
        settingsService.saveSettings(ModelMockHelper.createSettings());

        assertEquals(1, settingsRepository.findAll().size());
    }

    @Test
    void testAddSettings_secondLocale() {
        Settings settings = settingsService.saveSettings(ModelMockHelper.createSettings());
        settings.getLanguageSettings().put(Locale.ENGLISH.getLanguage(), ModelMockHelper.createLanguageSettings());
        settingsService.saveSettings(settings);

        List<Settings> savedSettings = settingsRepository.findAll();

        assertEquals(1, savedSettings.size());
        assertEquals(2, savedSettings.get(0).getLanguageSettings().size());
    }

    @Test
    void testGetSettings() {
        Settings settings = ModelMockHelper.createSettings();
        settingsService.saveSettings(settings);

        Settings loadedSettings = settingsService.getSettings();

        assertEquals(settings.getAdminSettings().getTandemsFrom(), loadedSettings.getAdminSettings().getTandemsFrom());
    }

    @Test
    void testGetSettings_NullIfNotFound() {
        Settings loadedSettings = settingsService.getSettings();

        assertNull(loadedSettings);
    }

    @Test
    void testGetAdminSettings() {
        Settings settings = saveExampleSettings();

        AdminSettings adminSettings = settingsService.getAdminSettings();

        assertNotNull(adminSettings);
        assertEquals(settings.getAdminSettings().getTandemsFrom(), adminSettings.getTandemsFrom());
    }

    @Test
    void testGetLanguageSettings() {
        Settings settings = saveExampleSettings();

        Map<String, LanguageSettings> commonSettings = settingsService.getLanguageSettings();

        assertEquals(1, commonSettings.size());
        assertEquals(settings.getLanguageSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(),
                commonSettings.get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl());
    }


    @Test
    void testGetAdminSettings_Null() {
        AdminSettings adminSettings = settingsService.getAdminSettings();

        assertNull(adminSettings);
    }

    @Test
    void testGetLanguageSettings_Empty() {
        Map<String, LanguageSettings> commonSettings = settingsService.getLanguageSettings();

        assertEquals(0, commonSettings.size());
    }

    @Test
    void testGetPublicSettingsByLanguage() {
        Settings settings = saveExampleSettings();

        PublicSettings publicSettings = settingsService.getPublicSettingsByLanguage(Locale.GERMAN.getLanguage());
        LanguageSettings languageSettings = publicSettings.getLanguageSettings();

        assertEquals(settings.getLanguageSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(), languageSettings.getDropzone().getPriceListUrl());
    }

    @Test
    void testGetPublicSettingsByLanguage_DefaultIfNotFound() {
        Settings settings = saveExampleSettings();

        PublicSettings publicSettings = settingsService.getPublicSettingsByLanguage(Locale.ENGLISH.getLanguage());
        LanguageSettings languageSettings = publicSettings.getLanguageSettings();

        assertNotNull(languageSettings);
        assertEquals(settings.getLanguageSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(), languageSettings.getDropzone().getPriceListUrl());
    }

    private Settings saveExampleSettings() {
        return settingsRepository.save(ModelMockHelper.createSettings());
    }

}
