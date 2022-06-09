package in.skdv.skdvinbackend.migration;

import in.skdv.skdvinbackend.migration.document.OldSettings;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * This migration migrates CommonSettings and LanguageSettings.
 */
@Slf4j
@ChangeUnit(id = "common-settings", order = "3")
@AllArgsConstructor
public class CommonSettingsMigration {

    private final MongoTemplate mongoTemplate;
    private final SettingsRepository settingsRepository;

    @Execution
    public void changeSet() {
        Query query = new Query();
        OldSettings oldSettings = mongoTemplate.findOne(query, OldSettings.class, "settings");

        if (oldSettings == null) {
            return;
        }

        CommonSettings commonSettings = CommonSettings.builder()
                .selfAssignmentMode(oldSettings.getCommonSettings().get("de").getSelfAssignmentMode())
                .picAndVidEnabled(true)
                .build();

        AdminSettings adminSettings = oldSettings.getAdminSettings();
        adminSettings.setBccMail(oldSettings.getCommonSettings().get("de").getBccMail());

        Map<String, LanguageSettings> languageSettings = new HashMap<>();
        oldSettings.getCommonSettings().forEach((k, v) -> {
            LanguageSettings ls = LanguageSettings.builder()
                    .additionalReminderHint(v.getAdditionalReminderHint())
                    .dropzone(v.getDropzone())
                    .faq(v.getFaq())
                    .homepageHint(v.getHomepageHint())
                    .homepageHintTitle(v.getHomepageHintTitle())
                    .build();
            languageSettings.put(k, ls);
        });

        Settings settings = new Settings();
        settings.setId(oldSettings.getId());
        settings.setCommonSettings(commonSettings);
        settings.setAdminSettings(adminSettings);
        settings.setLanguageSettings(languageSettings);

        mongoTemplate.save(settings, "settings");
    }

    @RollbackExecution
    public void rollback() {
        // nothing to do here
    }
}
