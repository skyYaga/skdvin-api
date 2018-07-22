package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.Jumpday;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JumpdayRepository extends MongoRepository<Jumpday, Integer> {
}
