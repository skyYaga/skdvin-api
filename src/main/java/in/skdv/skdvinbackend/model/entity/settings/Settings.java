package in.skdv.skdvinbackend.model.entity.settings;

import in.skdv.skdvinbackend.model.common.AbstractSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Locale;
import java.util.Map;

@Document
@Data
@EqualsAndHashCode(callSuper = true)
public class Settings extends AbstractSettings {

    private static final Locale DEFAULT_LOCALE = Locale.GERMAN;

    @Id
    private String id;

    public LanguageSettings getLanguageSettingsByLocaleOrDefault(String language) {
        Map<String, LanguageSettings> languageSettings = getLanguageSettings();
        if (languageSettings != null) {
            LanguageSettings localeLanguageSettings = languageSettings.get(language);
            if (localeLanguageSettings == null) {
                localeLanguageSettings = languageSettings.get(DEFAULT_LOCALE.getLanguage());
            }
            return localeLanguageSettings;
        }
        return null;
    }
}