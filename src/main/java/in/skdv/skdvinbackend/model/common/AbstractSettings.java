package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import lombok.Data;

import java.util.Map;

@Data
public class AbstractSettings {

    private AdminSettings adminSettings;
    private CommonSettings commonSettings;
    private Map<String, LanguageSettings> languageSettings;

}
