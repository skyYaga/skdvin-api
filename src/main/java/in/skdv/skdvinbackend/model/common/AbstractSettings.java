package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;

import java.util.Map;

public class AbstractSettings {

    private AdminSettings adminSettings;
    private Map<String, CommonSettings> commonSettings;

    public AdminSettings getAdminSettings() {
        return adminSettings;
    }

    public void setAdminSettings(AdminSettings adminSettings) {
        this.adminSettings = adminSettings;
    }

    public Map<String, CommonSettings> getCommonSettings() {
        return commonSettings;
    }

    public void setCommonSettings(Map<String, CommonSettings> commonSettings) {
        this.commonSettings = commonSettings;
    }
}
