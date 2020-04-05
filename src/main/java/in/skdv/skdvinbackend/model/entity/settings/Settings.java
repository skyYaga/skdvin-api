package in.skdv.skdvinbackend.model.entity.settings;

import org.springframework.data.annotation.Id;

import java.util.Locale;

public class Settings {

    @Id
    private Locale locale = Locale.GERMAN;
    private AdminSettings adminSettings;
    private CommonSettings commonSettings;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public AdminSettings getAdminSettings() {
        return adminSettings;
    }

    public void setAdminSettings(AdminSettings adminSettings) {
        this.adminSettings = adminSettings;
    }

    public CommonSettings getCommonSettings() {
        return commonSettings;
    }

    public void setCommonSettings(CommonSettings commonSettings) {
        this.commonSettings = commonSettings;
    }
}
