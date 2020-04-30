package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VideoflyerRepository extends MongoRepository<Videoflyer, String> {

    Optional<Videoflyer> findByEmail(String email);
}
