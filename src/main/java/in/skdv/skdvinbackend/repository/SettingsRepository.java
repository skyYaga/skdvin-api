package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.settings.Settings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Locale;

public interface SettingsRepository extends MongoRepository<Settings, String> {
    Settings findByLocale(Locale locale);
}
