package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoSettingsServiceTest extends AbstractSkdvinTest {

    @Autowired
    private ISettingsService settingsService;

    @Autowired
    private SettingsRepository settingsRepository;

    @Before
    public void setup() {
        settingsRepository.deleteAll();
    }

    @Test
    public void testAddSettings() {
        Settings settings = ModelMockHelper.createSettings();

        Settings saved = settingsService.saveSettings(settings);

        Assert.assertNotNull(saved.getId());
        Assert.assertEquals(5, saved.getAdminSettings().getTandemCount());
        Assert.assertEquals("Example DZ", saved.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getName());
    }

    @Test
    public void testAddSettings_OverridesFirst() {
        settingsService.saveSettings(ModelMockHelper.createSettings());
        settingsService.saveSettings(ModelMockHelper.createSettings());

        Assert.assertEquals(1, settingsRepository.findAll().size());
    }

    @Test
    public void testAddSettings_secondLocale() {
        Settings settings = settingsService.saveSettings(ModelMockHelper.createSettings());
        settings.getCommonSettings().put(Locale.ENGLISH.getLanguage(), ModelMockHelper.createCommonSettings());
        settingsService.saveSettings(settings);

        List<Settings> savedSettings = settingsRepository.findAll();

        Assert.assertEquals(1, savedSettings.size());
        Assert.assertEquals(2, savedSettings.get(0).getCommonSettings().size());
    }

    @Test
    public void testGetSettings() {
        Settings settings = ModelMockHelper.createSettings();
        settingsService.saveSettings(settings);

        Settings loadedSettings = settingsService.getSettings();

        Assert.assertEquals(settings.getAdminSettings().getTandemsFrom(), loadedSettings.getAdminSettings().getTandemsFrom());
    }

    @Test
    public void testGetSettings_NullIfNotFound() {
        Settings loadedSettings = settingsService.getSettings();

        Assert.assertNull(loadedSettings);
    }

    @Test
    public void testGetAdminSettings() {
        Settings settings = saveExampleSettings();

        AdminSettings adminSettings = settingsService.getAdminSettings();

        Assert.assertNotNull(adminSettings);
        Assert.assertEquals(settings.getAdminSettings().getTandemsFrom(), adminSettings.getTandemsFrom());
    }

    @Test
    public void testGetCommonSettings() {
        Settings settings = saveExampleSettings();

        Map<String, CommonSettings> commonSettings = settingsService.getCommonSettings();

        Assert.assertEquals(1, commonSettings.size());
        Assert.assertEquals(settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(),
                commonSettings.get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl());
    }

    @Test
    public void testGetAdminSettings_Null() {
        AdminSettings adminSettings = settingsService.getAdminSettings();

        Assert.assertNull(adminSettings);
    }

    @Test
    public void testGetCommonSettings_Empty() {
        Map<String, CommonSettings> commonSettings = settingsService.getCommonSettings();

        Assert.assertEquals(0, commonSettings.size());
    }

    @Test
    public void testGetCommonSettingsByLocale() {
        Settings settings = saveExampleSettings();

        CommonSettings commonSettings = settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage());

        Assert.assertEquals(settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(), commonSettings.getDropzone().getPriceListUrl());
    }

    @Test
    public void testGetCommonSettingsByLocale_DefaultIfNotFound() {
        Settings settings = saveExampleSettings();

        CommonSettings commonSettings = settingsService.getCommonSettingsByLanguage(Locale.ENGLISH.getLanguage());

        Assert.assertNotNull(commonSettings);
        Assert.assertEquals(settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().getPriceListUrl(), commonSettings.getDropzone().getPriceListUrl());
    }

    private Settings saveExampleSettings() {
        return settingsRepository.save(ModelMockHelper.createSettings());
    }

}
