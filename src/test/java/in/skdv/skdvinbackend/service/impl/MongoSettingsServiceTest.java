package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
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
public class MongoSettingsServiceTest extends AbstractSkdvinTest {

    @Autowired
    private ISettingsService settingsService;

    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setup() {
        settingsRepository.deleteAll();
    }

    @Test
    public void testAddSettings() {
        Settings settings = ModelMockHelper.createSettings();

        Settings saved = settingsService.saveSettings(settings);

        assertNotNull(saved.getId());
        assertEquals(5, saved.getAdminSettings().getTandemCount());
        assertEquals("Example DZ", saved.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getName());
    }

    @Test
    public void testAddSettings_OverridesFirst() {
        settingsService.saveSettings(ModelMockHelper.createSettings());
        settingsService.saveSettings(ModelMockHelper.createSettings());

        assertEquals(1, settingsRepository.findAll().size());
    }

    @Test
    public void testAddSettings_secondLocale() {
        Settings settings = settingsService.saveSettings(ModelMockHelper.createSettings());
        settings.getCommonSettings().put(Locale.ENGLISH.getLanguage(), ModelMockHelper.createCommonSettings());
        settingsService.saveSettings(settings);

        List<Settings> savedSettings = settingsRepository.findAll();

        assertEquals(1, savedSettings.size());
        assertEquals(2, savedSettings.get(0).getCommonSettings().size());
    }

    @Test
    public void testGetSettings() {
        Settings settings = ModelMockHelper.createSettings();
        settingsService.saveSettings(settings);

        Settings loadedSettings = settingsService.getSettings();

        assertEquals(settings.getAdminSettings().getTandemsFrom(), loadedSettings.getAdminSettings().getTandemsFrom());
    }

    @Test
    public void testGetSettings_NullIfNotFound() {
        Settings loadedSettings = settingsService.getSettings();

        assertNull(loadedSettings);
    }

    @Test
    public void testGetAdminSettings() {
        Settings settings = saveExampleSettings();

        AdminSettings adminSettings = settingsService.getAdminSettings();

        assertNotNull(adminSettings);
        assertEquals(settings.getAdminSettings().getTandemsFrom(), adminSettings.getTandemsFrom());
    }

    @Test
    public void testGetCommonSettings() {
        Settings settings = saveExampleSettings();

        Map<String, CommonSettings> commonSettings = settingsService.getCommonSettings();

        assertEquals(1, commonSettings.size());
        assertEquals(settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(),
                commonSettings.get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl());
    }

    @Test
    public void testGetAdminSettings_Null() {
        AdminSettings adminSettings = settingsService.getAdminSettings();

        assertNull(adminSettings);
    }

    @Test
    public void testGetCommonSettings_Empty() {
        Map<String, CommonSettings> commonSettings = settingsService.getCommonSettings();

        assertEquals(0, commonSettings.size());
    }

    @Test
    public void testGetCommonSettingsByLocale() {
        Settings settings = saveExampleSettings();

        CommonSettings commonSettings = settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage());

        assertEquals(settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(), commonSettings.getDropzone().getPriceListUrl());
    }

    @Test
    public void testGetCommonSettingsByLocale_DefaultIfNotFound() {
        Settings settings = saveExampleSettings();

        CommonSettings commonSettings = settingsService.getCommonSettingsByLanguage(Locale.ENGLISH.getLanguage());

        assertNotNull(commonSettings);
        assertEquals(settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(), commonSettings.getDropzone().getPriceListUrl());
    }

    private Settings saveExampleSettings() {
        return settingsRepository.save(ModelMockHelper.createSettings());
    }

}
