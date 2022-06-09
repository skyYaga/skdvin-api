package in.skdv.skdvinbackend.model.domain;

import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import lombok.Value;

@Value
public class PublicSettings {
    CommonSettings commonSettings;
    LanguageSettings languageSettings;
}
