package in.skdv.skdvinbackend.model.entity.settings;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Locale;
import java.util.Map;

@Document
public class Settings {

    @Id
    private String id;
    private AdminSettings adminSettings;
    private Map<Locale, CommonSettings> commonSettings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AdminSettings getAdminSettings() {
        return adminSettings;
    }

    public void setAdminSettings(AdminSettings adminSettings) {
        this.adminSettings = adminSettings;
    }

    public Map<Locale, CommonSettings> getCommonSettings() {
        return commonSettings;
    }

    public void setCommonSettings(Map<Locale, CommonSettings> commonSettings) {
        this.commonSettings = commonSettings;
    }
}
