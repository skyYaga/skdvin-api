package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.settings.Settings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SettingsRepository extends MongoRepository<Settings, String> {
}
