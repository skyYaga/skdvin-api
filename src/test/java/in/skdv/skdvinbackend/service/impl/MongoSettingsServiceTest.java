package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Dropzone;
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

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
        List<Settings> settings = createExampleSettings();

        List<Settings> savedList = settingsService.saveSettings(settings);

        Settings saved = savedList.get(0);
        Assert.assertNotNull(saved.getLocale());
        Assert.assertEquals(5, saved.getAdminSettings().getTandemCount());
        Assert.assertEquals("Example DZ", saved.getCommonSettings().getDropzone().getName());
    }

    @Test
    public void testAddSettings_OverridesFirst() {
        settingsService.saveSettings(createExampleSettings());
        settingsService.saveSettings(createExampleSettings());

        Assert.assertEquals(1, settingsRepository.findAll().size());
    }

    @Test
    public void testAddSettings_secondLocale() {
        settingsService.saveSettings(createExampleSettings());
        settingsService.saveSettings(createExampleSettings(Locale.ENGLISH));

        Assert.assertEquals(2, settingsRepository.findAll().size());
    }

    @Test
    public void testGetSettings() {
        List<Settings> settings = createExampleSettings();
        settingsService.saveSettings(settings);

        List<Settings> loadedSettings = settingsService.getSettings();

        Assert.assertEquals(1, settings.size());
        Assert.assertEquals(1, loadedSettings.size());
        Assert.assertEquals(settings.get(0).getAdminSettings().getTandemsFrom(), loadedSettings.get(0).getAdminSettings().getTandemsFrom());
    }

    @Test
    public void testGetSettings_EmptyListIfNotFound() {
        List<Settings> loadedSettings = settingsService.getSettings();

        Assert.assertEquals(0, loadedSettings.size());
    }

    @Test
    public void testGetAdminSettings() {
        Settings settings = saveExampleSettings();

        List<AdminSettings> adminSettings = settingsService.getAdminSettings();

        Assert.assertEquals(1, adminSettings.size());
        Assert.assertEquals(settings.getAdminSettings().getTandemsFrom(), adminSettings.get(0).getTandemsFrom());
    }

    @Test
    public void testGetCommonSettings() {
        Settings settings = saveExampleSettings();

        List<CommonSettings> commonSettings = settingsService.getCommonSettings();

        Assert.assertEquals(1, commonSettings.size());
        Assert.assertEquals(settings.getCommonSettings().getDropzone().getPriceListUrl(), commonSettings.get(0).getDropzone().getPriceListUrl());
    }

    @Test
    public void testGetAdminSettings_Null() {
        List<AdminSettings> adminSettings = settingsService.getAdminSettings();

        Assert.assertEquals(0, adminSettings.size());
    }

    @Test
    public void testGetCommonSettings_Null() {
        List<CommonSettings> commonSettings = settingsService.getCommonSettings();

        Assert.assertEquals(0, commonSettings.size());
    }

    @Test
    public void testGetAdminSettingsByLocale() {
        Settings settings = saveExampleSettings();

        AdminSettings adminSettings = settingsService.getAdminSettingsByLocale(Locale.GERMAN);

        Assert.assertEquals(settings.getAdminSettings().getTandemsFrom(), adminSettings.getTandemsFrom());
    }

    @Test
    public void testGetCommonSettingsByLocale() {
        Settings settings = saveExampleSettings();

        CommonSettings commonSettings = settingsService.getCommonSettingsByLocale(Locale.GERMAN);

        Assert.assertEquals(settings.getCommonSettings().getDropzone().getPriceListUrl(), commonSettings.getDropzone().getPriceListUrl());
    }

    @Test
    public void testGetAdminSettingsByLocale_DefaultIfNotFound() {
        Settings settings = saveExampleSettings();

        AdminSettings adminSettings = settingsService.getAdminSettingsByLocale(Locale.ENGLISH);

        Assert.assertNotNull(adminSettings);
        Assert.assertEquals(settings.getAdminSettings().getTandemsFrom(), adminSettings.getTandemsFrom());
    }

    @Test
    public void testGetCommonSettingsByLocale_DefaultIfNotFound() {
        Settings settings = saveExampleSettings();

        CommonSettings commonSettings = settingsService.getCommonSettingsByLocale(Locale.ENGLISH);

        Assert.assertNotNull(commonSettings);
        Assert.assertEquals(settings.getCommonSettings().getDropzone().getPriceListUrl(), commonSettings.getDropzone().getPriceListUrl());
    }

    private Settings saveExampleSettings() {
        return settingsRepository.save(createExampleSettings().get(0));
    }

    private List<Settings> createExampleSettings() {
        return createExampleSettings(Locale.GERMAN);
    }

    private List<Settings> createExampleSettings(Locale locale) {
        AdminSettings adminSettings = new AdminSettings();
        adminSettings.setTandemsFrom(LocalTime.of(10, 0));
        adminSettings.setTandemsTo(LocalTime.of(18, 0));
        adminSettings.setInterval(Duration.ofMinutes(90));
        adminSettings.setTandemCount(5);
        adminSettings.setPicOrVidCount(2);
        adminSettings.setPicAndVidCount(0);
        adminSettings.setHandcamCount(0);

        Dropzone dropzone = new Dropzone();
        dropzone.setName("Example DZ");
        dropzone.setPriceListUrl("https://example.com");

        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setDropzone(dropzone);

        Settings settings = new Settings();
        settings.setLocale(locale);
        settings.setAdminSettings(adminSettings);
        settings.setCommonSettings(commonSettings);

        return Collections.singletonList(settings);
    }
}
